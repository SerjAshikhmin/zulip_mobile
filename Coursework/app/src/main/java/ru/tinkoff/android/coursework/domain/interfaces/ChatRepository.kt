package ru.tinkoff.android.coursework.domain.interfaces

import io.reactivex.Single
import okhttp3.MultipartBody
import ru.tinkoff.android.coursework.data.api.model.response.ActionWithMessageResponse
import ru.tinkoff.android.coursework.data.api.model.response.ReactionResponse
import ru.tinkoff.android.coursework.data.api.model.response.SendMessageResponse
import ru.tinkoff.android.coursework.data.api.model.response.UploadFileResponse
import ru.tinkoff.android.coursework.domain.model.Message

internal interface ChatRepository {

    fun loadMessagesFromDb(streamName: String, topicName: String): Single<List<Message>>

    fun loadMessagesFromApi(
        streamName: String,
        topicName: String,
        anchor: Long,
        numOfMessagesInPortion: Int
    ): Single<List<Message>>

    fun saveMessagesToDb(messages: List<Message>)

    fun removeAllMessagesInTopicFromDb(topicName: String)

    fun removeAllMessagesInStreamFromDb(streamName: String)

    fun sendMessage(
        topic: String,
        stream: String,
        content: String
    ): Single<SendMessageResponse>

    fun addReaction(
        messageId: Long,
        emojiName: String
    ): Single<ReactionResponse>

    fun removeReaction(
        messageId: Long,
        emojiName: String
    ): Single<ReactionResponse>

    fun uploadFile(fileBody: MultipartBody.Part): Single<UploadFileResponse>

    fun loadSingleMessageFromApi(messageId: Long): Single<Message>

    fun deleteMessage(messageId: Long): Single<ActionWithMessageResponse>

    fun editMessage(
        messageId: Long,
        topicName: String,
        content: String
    ): Single<ActionWithMessageResponse>

}
