package ru.tinkoff.android.coursework.data

import android.content.Context
import android.util.Log
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.data.api.ZulipJsonApi
import ru.tinkoff.android.coursework.data.api.model.request.NarrowRequest
import ru.tinkoff.android.coursework.data.api.model.response.ReactionResponse
import ru.tinkoff.android.coursework.data.api.model.response.SendMessageResponse
import ru.tinkoff.android.coursework.data.api.model.response.UploadFileResponse
import ru.tinkoff.android.coursework.data.api.model.toMessageDbList
import ru.tinkoff.android.coursework.data.db.AppDatabase
import ru.tinkoff.android.coursework.data.db.model.Message
import ru.tinkoff.android.coursework.presentation.screens.ChatActivity
import javax.inject.Inject

internal class ChatRepositoryImpl @Inject constructor(
    private val applicationContext: Context,
    private val zulipJsonApi: ZulipJsonApi,
    private val db: AppDatabase
) : ChatRepository {

    override fun loadMessagesFromDb(topicName: String): Observable<List<Message>> {
        return db.messageDao().getAllByTopic(topicName)
            .onErrorReturn {
                Log.e(TAG, "Loading messages from db error", it)
                emptyList()
            }
            .toObservable()
    }

    override fun loadMessagesFromApi(
        topicName: String,
        currentAnchor: Long
    ): Observable<List<Message>> {
        return zulipJsonApi.getMessages(
            numBefore = ZulipJsonApi.NUMBER_OF_MESSAGES_BEFORE_ANCHOR,
            anchor = currentAnchor.toString(),
            narrow = arrayOf(
                NarrowRequest(
                    operator = ChatActivity.TOPIC_NARROW_OPERATOR_KEY,
                    operand = topicName
                )
            ).contentToString()
        )
            .map { it.messages.toMessageDbList() }
            .toObservable()
    }

    override fun removeRedundantMessagesFromDb(topicName: String, actualMessageIds: List<Long>) {
        db.messageDao().removeRedundant(topicName, actualMessageIds)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorComplete {
                Log.e(TAG, "Removing messages from db error", it)
                true
            }
            .subscribe()
    }

    override fun saveMessagesToDb(messages: List<Message>) {
        db.messageDao().saveAll(messages)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorReturn {
                Log.e(TAG, "Saving messages to db error", it)
                listOf()
            }
            .subscribe()
    }

    override fun sendMessage(
        topic: String,
        stream: String,
        content: String
    ): Single<SendMessageResponse> {
        return zulipJsonApi.sendMessage(
            to = stream,
            content = content,
            topic = topic
        )
    }

    override fun addReaction(messageId: Long, emojiName: String): Single<ReactionResponse> {
        return zulipJsonApi.addReaction(
            messageId = messageId,
            emojiName = emojiName
        )
    }

    override fun removeReaction(messageId: Long, emojiName: String): Single<ReactionResponse> {
        return zulipJsonApi.removeReaction(
            messageId = messageId,
            emojiName = emojiName
        )
    }

    override fun uploadFile(fileBody: MultipartBody.Part): Single<UploadFileResponse> {
        return zulipJsonApi.uploadFile(fileBody)
            .doOnError {
                Log.e(TAG, applicationContext.resources.getString(R.string.uploading_file_error_text), it)
            }
    }

    companion object {

        private const val TAG = "ChatRepository"
    }

}
