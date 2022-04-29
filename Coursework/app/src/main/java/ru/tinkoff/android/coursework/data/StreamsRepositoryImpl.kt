package ru.tinkoff.android.coursework.data

import android.util.Log
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.tinkoff.android.coursework.data.api.ZulipJsonApi
import ru.tinkoff.android.coursework.data.api.model.StreamDto
import ru.tinkoff.android.coursework.data.api.model.response.TopicsListResponse
import ru.tinkoff.android.coursework.data.db.AppDatabase
import ru.tinkoff.android.coursework.data.db.model.Stream
import ru.tinkoff.android.coursework.data.db.model.toStreamsDtoList
import javax.inject.Inject

internal class StreamsRepositoryImpl @Inject constructor(
    private val zulipJsonApi: ZulipJsonApi,
    private val db: AppDatabase
) : StreamsRepository {

    override fun loadStreamsFromDb(): Single<List<StreamDto>> {
        return db.streamDao().getAll()
            .map { it.toStreamsDtoList() }
            .onErrorReturn {
                Log.e(TAG, "Loading streams from db error", it)
                emptyList()
            }
    }

    override fun loadStreamsFromApi(isSubscribedStreams: Boolean): Single<List<StreamDto>> {
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

    private fun getTopicsInStream(stream: StreamDto): Single<StreamDto> {
        return zulipJsonApi.getTopicsInStream(streamId = stream.streamId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                stream.topics = it.topics
                saveStreamsToDb(stream.toStreamDb())
            }
            .onErrorReturn {
                Log.e(TAG, "Topics not found", it)
                TopicsListResponse(emptyList())
            }
            .map { stream }
    }

    private fun saveStreamsToDb(stream: Stream) {
        db.streamDao().save(stream)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorReturn {
                Log.e(TAG, "Saving streams to db error", it)
                DEFAULT_STREAM_ID
            }
            .subscribe()
    }

    companion object {

        private const val TAG = "StreamsRepository"
        private const val DEFAULT_STREAM_ID = 0L
    }

}
