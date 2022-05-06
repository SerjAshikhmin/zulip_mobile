package ru.tinkoff.android.coursework.domain.chat

import android.util.Log
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import ru.tinkoff.android.coursework.data.ChatRepository
import ru.tinkoff.android.coursework.data.api.model.response.ReactionResponse
import ru.tinkoff.android.coursework.data.api.model.response.SendMessageResponse
import ru.tinkoff.android.coursework.data.api.model.response.UploadFileResponse
import ru.tinkoff.android.coursework.domain.model.Message

internal class ChatInteractor(
    private val chatRepository: ChatRepository
) {

    fun loadLastMessages(
        topicName: String,
        anchor: Long
    ): Observable<List<Message>> {
        return Observable.merge(
            chatRepository.loadMessagesFromDb(topicName)
                .toObservable(),
            chatRepository.loadMessagesFromApi(
                topicName,
                anchor,
                NUMBER_OF_MESSAGES_IN_LAST_PORTION
            )
                .doOnSuccess {
                    if (it.isNotEmpty()) {
                        cacheMessages(it, topicName)
                    }
                }
                .toObservable()
        )
            .subscribeOn(Schedulers.io())
    }

    fun loadPortionOfMessages(
        topicName: String,
        anchor: Long
    ): Observable<List<Message>> {
        return chatRepository.loadMessagesFromApi(
            topicName,
            anchor,
            NUMBER_OF_MESSAGES_PER_PORTION
        )
            .toObservable()
            .subscribeOn(Schedulers.io())
    }

    fun loadMessage(messageId: Long): Observable<Message> {
        return chatRepository.loadSingleMessageFromApi(messageId)
            .toObservable()
            .subscribeOn(Schedulers.io())
    }

    fun sendMessage(
        topic: String,
        stream: String,
        content: String
    ): Single<SendMessageResponse> {
        return chatRepository.sendMessage(topic, stream, content)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun addReaction(
        messageId: Long,
        emojiName: String
    ): Single<ReactionResponse> {
        return chatRepository.addReaction(messageId, emojiName)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError {
                Log.e(TAG, "Adding emoji error", it)
            }
    }

    fun removeReaction(
        messageId: Long,
        emojiName: String
    ): Single<ReactionResponse> {
        return chatRepository.removeReaction(messageId, emojiName)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError {
                Log.e(TAG, "Removing emoji error", it)
            }
    }

    fun uploadFile(fileBody: MultipartBody.Part): Single<UploadFileResponse> {
        return chatRepository.uploadFile(fileBody)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    private fun cacheMessages(
        messages: List<Message>,
        topicName: String
    ) {
        chatRepository.removeAllMessagesInTopicFromDb(topicName)
        chatRepository.saveMessagesToDb(messages.takeLast(MAX_NUMBER_OF_MESSAGES_IN_CACHE))
    }

    companion object {

        private const val TAG = "ChatUseCases"
        private const val MAX_NUMBER_OF_MESSAGES_IN_CACHE = 50
        private const val NUMBER_OF_MESSAGES_PER_PORTION = 20
        private const val NUMBER_OF_MESSAGES_IN_LAST_PORTION = MAX_NUMBER_OF_MESSAGES_IN_CACHE
    }

}
