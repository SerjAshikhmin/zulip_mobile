package ru.tinkoff.android.coursework.data

import android.util.Log
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.tinkoff.android.coursework.data.api.ZulipJsonApi
import ru.tinkoff.android.coursework.data.api.model.StreamDto
import ru.tinkoff.android.coursework.data.api.model.response.TopicsListResponse
import ru.tinkoff.android.coursework.data.db.AppDatabase
import ru.tinkoff.android.coursework.data.mappers.StreamMapper
import ru.tinkoff.android.coursework.domain.model.Stream
import javax.inject.Inject

internal class StreamsRepositoryImpl @Inject constructor(
    private val zulipJsonApi: ZulipJsonApi,
    private val db: AppDatabase
) : StreamsRepository {

    override fun loadStreamsFromDb(): Single<List<Stream>> {
        return db.streamDao().getAll()
            .map { StreamMapper.streamsDbToStreamsList(it) }
            .onErrorReturn {
                Log.e(TAG, "Loading streams from db error", it)
                emptyList()
            }
    }

    override fun loadStreamsFromApi(isSubscribedStreams: Boolean): Single<List<Stream>> {
        val baseStream = if (isSubscribedStreams) {
            zulipJsonApi.getSubscribedStreams()
                .flattenAsObservable { it.subscriptions }
        } else {
            zulipJsonApi.getAllStreams()
                .flattenAsObservable { it.streams }
        }
        return baseStream
            .flatMapSingle { getTopicsInStream(it) }
            .doOnError {
                Log.e(TAG, "Loading streams from api error", it)
            }
            .toList()
    }

    override fun saveStreamsToDb(streams: List<Stream>) {
        db.streamDao().saveAll(StreamMapper.streamsToStreamsDbList(streams))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorReturn {
                Log.e(TAG, "Saving streams to db error", it)
                emptyList()
            }
            .subscribe()
    }

    private fun getTopicsInStream(stream: StreamDto): Single<Stream> {
        return zulipJsonApi.getTopicsInStream(streamId = stream.streamId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                stream.topics = it.topics
            }
            .onErrorReturn {
                Log.e(TAG, "Topics not found", it)
                TopicsListResponse(emptyList())
            }
            .map { StreamMapper.streamDtoToStream(stream) }
    }

    companion object {

        private const val TAG = "StreamsRepository"
    }

}
