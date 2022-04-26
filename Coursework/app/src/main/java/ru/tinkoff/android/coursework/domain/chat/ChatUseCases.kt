package ru.tinkoff.android.coursework.domain.chat

import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.MultipartBody
import ru.tinkoff.android.coursework.data.ChatRepository
import ru.tinkoff.android.coursework.data.api.ZulipJsonApi
import ru.tinkoff.android.coursework.data.api.model.response.ReactionResponse
import ru.tinkoff.android.coursework.data.api.model.response.SendMessageResponse
import ru.tinkoff.android.coursework.data.api.model.response.UploadFileResponse
import ru.tinkoff.android.coursework.data.db.model.Message

internal class ChatUseCases(
    private val chatRepository: ChatRepository
) {

    fun loadMessages(
        topicName: String,
        currentAnchor: Long,
        updateAllMessages: Boolean
    ): Observable<List<Message>> {
        return Observable.merge(
            if (updateAllMessages) {
                chatRepository.loadMessagesFromDb(topicName)
            } else Observable.empty(),
            chatRepository.loadMessagesFromApi(topicName, currentAnchor)
        )
    }

    fun cacheMessages(
        newMessages: List<Message>,
        actualMessages: List<Message>,
        topicName: String
    ) {
        if (actualMessages.size <= MAX_NUMBER_OF_MESSAGES_IN_CACHE) {
            val remainingMessagesLimit = (MAX_NUMBER_OF_MESSAGES_IN_CACHE - actualMessages.size)
                .coerceAtMost(NUMBER_OF_MESSAGES_PER_PORTION)
            chatRepository.saveMessagesToDb(newMessages.takeLast(remainingMessagesLimit))
        } else {
            chatRepository.saveMessagesToDb(actualMessages.takeLast(MAX_NUMBER_OF_MESSAGES_IN_CACHE))
            val actualMessageIds = actualMessages
                .takeLast(MAX_NUMBER_OF_MESSAGES_IN_CACHE).map { it.id }
            chatRepository.removeRedundantMessagesFromDb(topicName, actualMessageIds)
        }
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

    companion object {

        private const val MAX_NUMBER_OF_MESSAGES_IN_CACHE = 50
        const val NUMBER_OF_MESSAGES_PER_PORTION = ZulipJsonApi.NUMBER_OF_MESSAGES_BEFORE_ANCHOR
    }

}
