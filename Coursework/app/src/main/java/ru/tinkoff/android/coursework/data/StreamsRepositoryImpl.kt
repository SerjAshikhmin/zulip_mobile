package ru.tinkoff.android.coursework.data

import android.util.Log
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.tinkoff.android.coursework.data.api.ZulipJsonApi
import ru.tinkoff.android.coursework.data.api.model.StreamDto
import ru.tinkoff.android.coursework.data.api.model.request.SubscriptionsRequest
import ru.tinkoff.android.coursework.data.api.model.response.SubscribeToStreamResponse
import ru.tinkoff.android.coursework.data.api.model.response.TopicsListResponse
import ru.tinkoff.android.coursework.data.db.AppDatabase
import ru.tinkoff.android.coursework.data.mappers.StreamMapper
import ru.tinkoff.android.coursework.domain.interfaces.StreamsRepository
import ru.tinkoff.android.coursework.domain.model.Stream
import javax.inject.Inject

internal class StreamsRepositoryImpl @Inject constructor(
    private val zulipJsonApi: ZulipJsonApi,
    private val db: AppDatabase
) : StreamsRepository {

    override fun loadStreamsFromDb(isSubscribedStreams: Boolean): Single<List<Stream>> {
        val streamsList = if (isSubscribedStreams) {
            db.streamDao().getAllSubscribed()
        } else {
            db.streamDao().getAll()
        }
        return streamsList
            .map { StreamMapper.streamsDbToStreamsList(it) }
            .onErrorReturn {
                Log.e(TAG, "Loading streams from db error", it)
                emptyList()
            }
    }

    override fun loadStreamsFromApi(isSubscribedStreams: Boolean): Single<List<Stream>> {
        val baseStream = if (isSubscribedStreams) {
            zulipJsonApi.getSubscribedStreams()
                .flattenAsObservable {
                    it.subscriptions.forEach { stream -> stream.isSubscribed = true }
                    it.subscriptions
                }
        } else {
            zulipJsonApi.getAllStreams()
                .flattenAsObservable {
                    it.streams.forEach { stream -> stream.isSubscribed = false }
                    it.streams
                }
        }
        return baseStream
            .flatMapSingle { getTopicsInStream(it) }
            .doOnError {
                Log.e(TAG, "Loading streams from api error", it)
            }
            .toList()
    }

    override fun saveStreamsToDb(streams: List<Stream>, isSubscribedStreams: Boolean) {
        val savedStreams = if (isSubscribedStreams) {
            streams.forEach { it.isSubscribed = isSubscribedStreams }
            db.streamDao().saveAllReplaceConflicts(
                StreamMapper.streamsToStreamsDbList(streams)
            )
        } else {
            db.streamDao().saveAllIgnoreConflicts(
                StreamMapper.streamsToStreamsDbList(streams)
            )
        }
        savedStreams
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorReturn {
                Log.e(TAG, "Saving streams to db error", it)
                emptyList()
            }
            .subscribe()
    }

    override fun createStream(
        name: String,
        description: String,
        isPrivate: Boolean
    ): Single<SubscribeToStreamResponse> {
        return zulipJsonApi.subscribeToStream(
            inviteOnly = isPrivate,
            subscriptions = arrayOf(
                SubscriptionsRequest(
                    name = name,
                    description = description
                )
            ).contentToString()
        )
            .doOnError {
                Log.e(TAG, "Stream creation error", it)
            }
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

    override fun deleteStreamsFromDb(isSubscribedStreams: Boolean) {
        db.streamDao().deleteAllBySubscribedSign(isSubscribedStreams)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorComplete {
                Log.e(TAG, "Deleting streams from db error", it)
                true
            }
            .subscribe()
    }

    companion object {

        private const val TAG = "StreamsRepository"
    }

}
