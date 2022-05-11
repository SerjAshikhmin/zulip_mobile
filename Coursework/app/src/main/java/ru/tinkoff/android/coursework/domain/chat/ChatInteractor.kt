package ru.tinkoff.android.coursework.domain.chat

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import ru.tinkoff.android.coursework.data.ChatRepository
import ru.tinkoff.android.coursework.data.api.model.response.DeleteMessageResponse
import ru.tinkoff.android.coursework.data.api.model.response.ReactionResponse
import ru.tinkoff.android.coursework.data.api.model.response.SendMessageResponse
import ru.tinkoff.android.coursework.data.api.model.response.UploadFileResponse
import ru.tinkoff.android.coursework.domain.model.Message

internal class ChatInteractor(
    private val chatRepository: ChatRepository
) {

    fun loadLastMessages(
        streamName: String,
        topicName: String,
        anchor: Long
    ): Observable<List<Message>> {
        return Observable.merge(
            chatRepository.loadMessagesFromDb(topicName)
                .toObservable(),
            chatRepository.loadMessagesFromApi(
                streamName,
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
        streamName: String,
        topicName: String,
        anchor: Long
    ): Observable<List<Message>> {
        return chatRepository.loadMessagesFromApi(
            streamName,
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
    }

    fun removeReaction(
        messageId: Long,
        emojiName: String
    ): Single<ReactionResponse> {
        return chatRepository.removeReaction(messageId, emojiName)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun uploadFile(fileBody: MultipartBody.Part): Single<UploadFileResponse> {
        return chatRepository.uploadFile(fileBody)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun deleteMessage(messageId: Long): Single<DeleteMessageResponse> {
        return chatRepository.deleteMessage(messageId)
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

        internal const val LAST_MESSAGE_ANCHOR = 10000000000000000L
        private const val MAX_NUMBER_OF_MESSAGES_IN_CACHE = 50
        private const val NUMBER_OF_MESSAGES_PER_PORTION = 20
        private const val NUMBER_OF_MESSAGES_IN_LAST_PORTION = MAX_NUMBER_OF_MESSAGES_IN_CACHE
    }

}
