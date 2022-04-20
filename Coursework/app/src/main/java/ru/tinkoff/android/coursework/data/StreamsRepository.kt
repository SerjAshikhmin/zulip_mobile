package ru.tinkoff.android.coursework.data

import android.content.Context
import android.util.Log
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.data.api.NetworkService
import ru.tinkoff.android.coursework.data.api.model.StreamDto
import ru.tinkoff.android.coursework.data.api.model.response.TopicsListResponse
import ru.tinkoff.android.coursework.data.db.AppDatabase
import ru.tinkoff.android.coursework.data.db.model.Stream
import ru.tinkoff.android.coursework.data.db.model.toStreamsDtoList

internal class StreamsRepository(private val applicationContext: Context) {

    private var db: AppDatabase? = AppDatabase.getAppDatabase(applicationContext)

    fun loadStreamsFromDb(): Single<List<StreamDto>>? {
        return db?.streamDao()?.getAll()
            ?.map { it.toStreamsDtoList() }
            ?.onErrorReturn {
                Log.e(TAG, applicationContext.resources.getString(R.string.loading_streams_from_db_error_text), it)
                emptyList()
            }
    }

    fun loadStreamsFromApi(isSubscribedStreams: Boolean): Single<List<StreamDto>> {
        if (isSubscribedStreams) {
            return NetworkService.getZulipJsonApi().getSubscribedStreams()
                .map { it.subscriptions }
                .flatMapObservable  { Observable.fromIterable(it)  }
                .flatMapSingle { getTopicsInStream(it) }
                .doOnError {
                    Log.e(TAG, applicationContext.resources.getString(R.string.loading_streams_from_api_error_text), it)
                }
                .toList()
        } else {
            return NetworkService.getZulipJsonApi().getAllStreams()
                .map { it.streams }
                .flatMapObservable  { Observable.fromIterable(it)  }
                .flatMapSingle { getTopicsInStream(it) }
                .doOnError {
                    Log.e(TAG, applicationContext.resources.getString(R.string.loading_streams_from_api_error_text), it)
                }
                .toList()
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
                Log.e(TAG, applicationContext.resources.getString(R.string.topics_not_found_error_text), it)
                TopicsListResponse(emptyList())
            }
            .map { stream }
    }

    private fun saveStreamsToDb(stream: Stream) {
        db?.streamDao()?.save(stream)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.onErrorReturn {
                Log.e(TAG, applicationContext.resources.getString(R.string.saving_streams_to_db_error_text), it)
                DEFAULT_STREAM_ID
            }
            ?.subscribe()
    }

    companion object {

        private const val TAG = "StreamsRepository"
        private const val DEFAULT_STREAM_ID = 0L
    }

}
