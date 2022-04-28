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

    private var currentCache: List<Message> = listOf()
    private var currentTopicName: String = ""

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
        messages: List<Message>,
        topicName: String
    ) {
        if (messages.isEmpty()) return
        if (currentTopicName != topicName) {
            // произошла смена топика - сбрасываем локальный кэш
            currentCache = listOf()
            currentTopicName = topicName
        }

        if (messages.size > NUMBER_OF_MESSAGES_PER_PORTION) {
            // пришел кэш - просто сохраняем локально
            currentCache = messages
        } else {
            // пришли данные из апи или кэш меньше мин. порции данных - обновляем кэш
            if (currentCache.isEmpty()) {
                // кэш, полученный при предыдущей загрузке, пустой - сохраняем все в БД и локально
                chatRepository.saveMessagesToDb(messages)
                currentCache = messages
            } else {
                if (currentCache[0].id > messages.last().id) {
                    // новая порция за диапазоном кэша
                    if (currentCache.size < MAX_NUMBER_OF_MESSAGES_IN_CACHE) {
                        // в кэше еще есть место - сохраняем, сколько сможем
                        val remainingCacheLimit = (MAX_NUMBER_OF_MESSAGES_IN_CACHE - currentCache.size)
                            .coerceAtMost(NUMBER_OF_MESSAGES_PER_PORTION)
                        val messagesToSave = messages.takeLast(remainingCacheLimit)
                        chatRepository.saveMessagesToDb(messagesToSave)
                        currentCache = currentCache.plus(messagesToSave)
                    }
                } else {
                    // новая порция сообщений из апи входит в диапазон кэша
                    if (currentCache[0].id <= messages[0].id) {
                        // новая порция целиком входит в кэш - обновляем эту часть кэша в БД
                        chatRepository.saveMessagesToDb(messages)
                    } else {
                        // новая порция частично входит в кэш - находим последнее сообщение
                        // в новой порции, входящее в кэш, и сохраняем в БД
                        var lastMessageToCashIndex = messages.lastIndex
                        while (messages[lastMessageToCashIndex].id > currentCache[0].id) {
                            lastMessageToCashIndex--
                        }
                        val messagesToSave = messages.takeLast(messages.lastIndex - lastMessageToCashIndex)
                        chatRepository.saveMessagesToDb(messagesToSave)
                        currentCache = currentCache.plus(messagesToSave)
                    }
                }
            }
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
