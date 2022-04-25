package ru.tinkoff.android.coursework.domain.chat

import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.MultipartBody
import ru.tinkoff.android.coursework.data.ChatRepository
import ru.tinkoff.android.coursework.data.api.model.response.ReactionResponse
import ru.tinkoff.android.coursework.data.api.model.response.SendMessageResponse
import ru.tinkoff.android.coursework.data.api.model.response.UploadFileResponse
import ru.tinkoff.android.coursework.data.db.model.Message

internal class ChatUseCases(
    private val chatRepository: ChatRepository
) {

    fun loadMessages(topicName: String, adapterAnchor: Long, isFirstPosition: Boolean): Observable<List<Message>> {
        return Observable.merge(
            if (isFirstPosition) {
                chatRepository.loadMessagesFromDb(topicName)
            } else Observable.empty(),
            chatRepository.loadMessagesFromApi(topicName, adapterAnchor)
        )
    }

    fun cacheMessages(
        newMessages: List<Message>,
        adapterMessages: List<Message>,
        topicName: String
    ) {
        chatRepository.cacheMessages(newMessages, adapterMessages, topicName)
    }

    fun sendMessage(topic: String, stream: String, content: String): Single<SendMessageResponse> {
        return chatRepository.sendMessage(topic, stream, content)
    }

    fun addReaction(messageId: Long, emojiName: String): Single<ReactionResponse> {
        return chatRepository.addReaction(messageId, emojiName)
    }

    fun removeReaction(messageId: Long, emojiName: String): Single<ReactionResponse> {
        return chatRepository.removeReaction(messageId, emojiName)
    }

    fun uploadFile(fileBody: MultipartBody.Part): Single<UploadFileResponse> {
        return chatRepository.uploadFile(fileBody)
    }

}
