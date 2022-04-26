package ru.tinkoff.android.coursework.data

import android.content.Context
import android.util.Log
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.tinkoff.android.coursework.data.api.NetworkService
import ru.tinkoff.android.coursework.data.api.model.StreamDto
import ru.tinkoff.android.coursework.data.api.model.response.TopicsListResponse
import ru.tinkoff.android.coursework.data.db.AppDatabase
import ru.tinkoff.android.coursework.data.db.model.Stream
import ru.tinkoff.android.coursework.data.db.model.toStreamsDtoList

internal class StreamsRepositoryImpl(private val applicationContext: Context) : StreamsRepository {

    private var db: AppDatabase? = AppDatabase.getAppDatabase(applicationContext)

    override fun loadStreamsFromDb(): Observable<List<StreamDto>>? {
        return db?.streamDao()?.getAll()
            ?.map { it.toStreamsDtoList() }
            ?.onErrorReturn {
                Log.e(TAG, "Loading streams from db error", it)
                emptyList()
            }
            ?.toObservable()
    }

    override fun loadStreamsFromApi(isSubscribedStreams: Boolean): Observable<List<StreamDto>> {
        if (isSubscribedStreams) {
            return NetworkService.getZulipJsonApi().getSubscribedStreams()
                .map { it.subscriptions }
                .flatMapObservable  { Observable.fromIterable(it)  }
                .flatMapSingle { getTopicsInStream(it) }
                .doOnError {
                    Log.e(TAG, "Loading streams from api error", it)
                }
                .toList()
                .toObservable()
        } else {
            return NetworkService.getZulipJsonApi().getAllStreams()
                .map { it.streams }
                .flatMapObservable  { Observable.fromIterable(it)  }
                .flatMapSingle { getTopicsInStream(it) }
                .doOnError {
                    Log.e(TAG, "Loading streams from api error", it)
                }
                .toList()
                .toObservable()
        }
    }

    private fun getTopicsInStream(stream: StreamDto): Single<StreamDto> {
        return NetworkService.getZulipJsonApi().getTopicsInStream(streamId = stream.streamId)
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
        db?.streamDao()?.save(stream)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.onErrorReturn {
                Log.e(TAG, "Saving streams to db error", it)
                DEFAULT_STREAM_ID
            }
            ?.subscribe()
    }

    companion object {

        private const val TAG = "StreamsRepository"
        private const val DEFAULT_STREAM_ID = 0L
    }

}
