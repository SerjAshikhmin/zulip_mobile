package ru.tinkoff.android.coursework.data

import android.util.Log
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import ru.tinkoff.android.coursework.data.api.ZulipJsonApi
import ru.tinkoff.android.coursework.data.api.model.request.NarrowRequest
import ru.tinkoff.android.coursework.data.api.model.response.ActionWithMessageResponse
import ru.tinkoff.android.coursework.data.api.model.response.ReactionResponse
import ru.tinkoff.android.coursework.data.api.model.response.SendMessageResponse
import ru.tinkoff.android.coursework.data.api.model.response.UploadFileResponse
import ru.tinkoff.android.coursework.data.db.AppDatabase
import ru.tinkoff.android.coursework.data.mappers.MessageMapper
import ru.tinkoff.android.coursework.domain.chat.ChatInteractor
import ru.tinkoff.android.coursework.domain.interfaces.ChatRepository
import ru.tinkoff.android.coursework.domain.model.Message
import ru.tinkoff.android.coursework.presentation.screens.StreamsListFragment.Companion.ALL_TOPICS_IN_STREAM
import javax.inject.Inject

internal class ChatRepositoryImpl @Inject constructor(
    private val zulipJsonApi: ZulipJsonApi,
    private val db: AppDatabase
) : ChatRepository {

    override fun loadMessagesFromDb(streamName: String, topicName: String): Single<List<Message>> {
        val messagesList = if (topicName == ALL_TOPICS_IN_STREAM) {
            db.messageDao().getAllByStream(streamName)
        } else {
            db.messageDao().getAllByTopic(topicName)
        }
        return messagesList
            .onErrorReturn {
                Log.e(TAG, "Loading messages from db error", it)
                emptyList()
            }
            .map { MessageMapper.messagesDbToMessagesList(it) }
    }

    override fun loadMessagesFromApi(
        streamName: String,
        topicName: String,
        anchor: Long,
        numOfMessagesInPortion: Int
    ): Single<List<Message>> {
        val narrow = if (topicName == ALL_TOPICS_IN_STREAM) {
            arrayOf(
                NarrowRequest(
                    operator = ZulipJsonApi.STREAM_NARROW_OPERATOR_KEY,
                    operand = streamName
                )
            ).contentToString()
        } else {
            arrayOf(
                NarrowRequest(
                    operator = ZulipJsonApi.TOPIC_NARROW_OPERATOR_KEY,
                    operand = topicName
                )
            ).contentToString()
        }
        return zulipJsonApi.getMessages(
            numBefore = numOfMessagesInPortion,
            anchor = if (anchor == ChatInteractor.LAST_MESSAGE_ANCHOR) {
                ZulipJsonApi.LAST_MESSAGE_ANCHOR.toString()
            } else {
                anchor.toString()
            },
            narrow = narrow
        )
            .map { MessageMapper.messagesDtoToMessagesList(it.messages) }
            .doOnError {
                Log.e(TAG, "Loading messages from api error", it)
            }
    }

    override fun removeAllMessagesInTopicFromDb(topicName: String) {
        db.messageDao().removeAllFromTopic(topicName)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorComplete {
                Log.e(TAG, "Removing messages from db error", it)
                true
            }
            .subscribe()
    }

    override fun removeAllMessagesInStreamFromDb(streamName: String) {
        db.messageDao().removeAllFromStream(streamName)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorComplete {
                Log.e(TAG, "Removing messages from db error", it)
                true
            }
            .subscribe()
    }

    override fun saveMessagesToDb(messages: List<Message>) {
        db.messageDao().saveAll(MessageMapper.toDbMessagesList(messages))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorReturn {
                Log.e(TAG, "Saving messages to db error", it)
                emptyList()
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
            .doOnError {
                Log.e(TAG, "Sending message error", it)
            }
    }

    override fun addReaction(messageId: Long, emojiName: String): Single<ReactionResponse> {
        return zulipJsonApi.addReaction(
            messageId = messageId,
            emojiName = emojiName
        )
            .doOnError {
                Log.e(TAG, "Adding reaction error", it)
            }
    }

    override fun removeReaction(messageId: Long, emojiName: String): Single<ReactionResponse> {
        return zulipJsonApi.removeReaction(
            messageId = messageId,
            emojiName = emojiName
        )
            .doOnError {
                Log.e(TAG, "Removing reaction error", it)
            }
    }

    override fun uploadFile(fileBody: MultipartBody.Part): Single<UploadFileResponse> {
        return zulipJsonApi.uploadFile(fileBody)
            .doOnError {
                Log.e(TAG, "Uploading file error", it)
            }
    }

    override fun loadSingleMessageFromApi(messageId: Long): Single<Message> {
        return zulipJsonApi.loadSingleMessage(messageId)
            .map { MessageMapper.messageDtoToMessage(it.message) }
            .doOnError {
                Log.e(TAG, "Loading message from api error", it)
            }
    }

    override fun deleteMessage(messageId: Long): Single<ActionWithMessageResponse> {
        return zulipJsonApi.deleteMessage(messageId)
            .doOnError {
                Log.e(TAG, "Deleting message error", it)
            }
    }

    override fun editMessage(
        messageId: Long,
        topicName: String,
        content: String
    ): Single<ActionWithMessageResponse> {
        return zulipJsonApi.editMessage(messageId, topicName, content)
            .doOnError {
                Log.e(TAG, "Editing message error", it)
            }
    }

    companion object {

        private const val TAG = "ChatRepository"
    }

}
