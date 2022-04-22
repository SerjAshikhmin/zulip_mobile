package ru.tinkoff.android.coursework.data

import android.content.Context
import android.util.Log
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.data.api.NetworkService
import ru.tinkoff.android.coursework.data.api.ZulipJsonApi
import ru.tinkoff.android.coursework.data.api.model.request.NarrowRequest
import ru.tinkoff.android.coursework.data.api.model.response.ReactionResponse
import ru.tinkoff.android.coursework.data.api.model.response.SendMessageResponse
import ru.tinkoff.android.coursework.data.api.model.response.UploadFileResponse
import ru.tinkoff.android.coursework.data.api.model.toMessageDbList
import ru.tinkoff.android.coursework.data.db.AppDatabase
import ru.tinkoff.android.coursework.data.db.model.Message
import ru.tinkoff.android.coursework.presentation.screens.ChatActivity

internal class ChatRepository(private val applicationContext: Context) {

    private var db: AppDatabase? = AppDatabase.getAppDatabase(applicationContext)

    fun loadMessagesFromDb(topicName: String): Single<List<Message>>? {
        return db?.messageDao()?.getAllByTopic(topicName)
            ?.onErrorReturn {
                Log.e(TAG, applicationContext.resources.getString(R.string.loading_messages_from_db_error_text), it)
                emptyList()
            }
    }

    fun loadMessagesFromApi(topicName: String, adapterAnchor: Long): Single<List<Message>> {
        return NetworkService.getZulipJsonApi().getMessages(
            numBefore = ZulipJsonApi.NUMBER_OF_MESSAGES_BEFORE_ANCHOR,
            anchor = adapterAnchor.toString(),
            narrow = arrayOf(
                NarrowRequest(
                    operator = ChatActivity.TOPIC_NARROW_OPERATOR_KEY,
                    operand = topicName
                )
            ).contentToString()
        )
            .map { it.messages.toMessageDbList() }
    }

    fun cacheMessages(newMessages: List<Message>, adapterMessages: List<Message>, topicName: String) {
        if (adapterMessages.size <= MAX_NUMBER_OF_MESSAGES_IN_CACHE) {
            val remainingMessagesLimit =
                if (MAX_NUMBER_OF_MESSAGES_IN_CACHE - adapterMessages.size > NUMBER_OF_MESSAGES_PER_PORTION) {
                    NUMBER_OF_MESSAGES_PER_PORTION
                } else {
                    MAX_NUMBER_OF_MESSAGES_IN_CACHE - adapterMessages.size
                }
            saveMessagesToDb(newMessages.takeLast(remainingMessagesLimit))
        } else {
            saveMessagesToDb(adapterMessages.takeLast(MAX_NUMBER_OF_MESSAGES_IN_CACHE))
            val actualMessageIds = adapterMessages.takeLast(MAX_NUMBER_OF_MESSAGES_IN_CACHE).map { it.id }
            removeRedundantMessagesFromDb(topicName, actualMessageIds)
        }
    }

    private fun removeRedundantMessagesFromDb(topicName: String, actualMessageIds: List<Long>) {
        db?.messageDao()?.removeRedundant(topicName, actualMessageIds)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.onErrorComplete {
                Log.e(TAG, applicationContext.resources.getString(R.string.removing_messages_from_db_error_text), it)
                true
            }
            ?.subscribe()
    }

    private fun saveMessagesToDb(messages: List<Message>) {
        db?.messageDao()?.saveAll(messages)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.onErrorReturn {
                Log.e(TAG, applicationContext.resources.getString(R.string.saving_messages_to_db_error_text), it)
                listOf()
            }
            ?.subscribe()
    }

    fun sendMessage(topic: String, stream: String, content: String): Single<SendMessageResponse> {
        return NetworkService.getZulipJsonApi().sendMessage(
            to = stream,
            content = content,
            topic = topic
        )
    }

    fun addReaction(messageId: Long, emojiName: String): Single<ReactionResponse> {
        return NetworkService.getZulipJsonApi().addReaction(
            messageId = messageId,
            emojiName = emojiName
        )
    }

    fun removeReaction(messageId: Long, emojiName: String): Single<ReactionResponse> {
        return NetworkService.getZulipJsonApi().removeReaction(
            messageId = messageId,
            emojiName = emojiName
        )
    }

    fun uploadFile(fileBody: MultipartBody.Part): Single<UploadFileResponse> {
        return NetworkService.getZulipJsonApi().uploadFile(fileBody)
            .doOnError {
                Log.e(TAG, applicationContext.resources.getString(R.string.uploading_file_error_text), it)
            }
    }

    companion object {

        private const val TAG = "ChatRepository"
        private const val MAX_NUMBER_OF_MESSAGES_IN_CACHE = 50
        private const val NUMBER_OF_MESSAGES_PER_PORTION = ZulipJsonApi.NUMBER_OF_MESSAGES_BEFORE_ANCHOR
    }

}
