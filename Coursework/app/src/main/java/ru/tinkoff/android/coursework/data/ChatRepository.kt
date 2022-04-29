package ru.tinkoff.android.coursework.data

import io.reactivex.Single
import okhttp3.MultipartBody
import ru.tinkoff.android.coursework.data.api.model.response.ReactionResponse
import ru.tinkoff.android.coursework.data.api.model.response.SendMessageResponse
import ru.tinkoff.android.coursework.data.api.model.response.UploadFileResponse
import ru.tinkoff.android.coursework.data.db.model.Message

internal interface ChatRepository {

    fun loadMessagesFromDb(topicName: String): Single<List<Message>>
    fun loadMessagesFromApi(topicName: String, currentAnchor: Long): Single<List<Message>>
    fun saveMessagesToDb(messages: List<Message>)
    fun removeRedundantMessagesFromDb(topicName: String, actualMessageIds: List<Long>)
    fun sendMessage(topic: String, stream: String, content: String): Single<SendMessageResponse>
    fun addReaction(messageId: Long, emojiName: String): Single<ReactionResponse>
    fun removeReaction(messageId: Long, emojiName: String): Single<ReactionResponse>
    fun uploadFile(fileBody: MultipartBody.Part): Single<UploadFileResponse>

}
