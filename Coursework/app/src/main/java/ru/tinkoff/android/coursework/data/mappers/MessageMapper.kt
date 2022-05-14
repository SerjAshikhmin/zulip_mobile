package ru.tinkoff.android.coursework.data.mappers

import ru.tinkoff.android.coursework.data.api.model.MessageDto
import ru.tinkoff.android.coursework.data.api.model.ReactionDto
import ru.tinkoff.android.coursework.data.api.model.SELF_USER_ID
import ru.tinkoff.android.coursework.data.db.model.MessageDb
import ru.tinkoff.android.coursework.domain.model.EmojiWithCount
import ru.tinkoff.android.coursework.domain.model.Message

internal object MessageMapper {

    fun toDbMessagesList(messages: List<Message>): List<MessageDb> =
        messages.map { message -> messageToMessageDb(message) }

    fun messagesDbToMessagesList(messagesDb: List<MessageDb>): List<Message> =
        messagesDb.map { message -> messageDbToMessage(message) }

    fun messagesDtoToMessagesList(messagesDto: List<MessageDto>): List<Message> =
        messagesDto.map { messageDto -> messageDtoToMessage(messageDto) }

    fun messageToMessageDb(message: Message): MessageDb {
        return MessageDb(
            id = message.id,
            userId = message.userId,
            userFullName = message.userFullName,
            topicName = message.topicName,
            streamId = message.streamId,
            avatarUrl = message.avatarUrl,
            content = message.content,
            emojis = EmojiMapper.emojisToEmojisDbList(message.emojis),
            timestamp = message.timestamp
        )
    }

    fun messageDbToMessage(messageDb: MessageDb): Message {
        return Message(
            id = messageDb.id,
            userId = messageDb.userId,
            userFullName = messageDb.userFullName,
            topicName = messageDb.topicName,
            streamId = messageDb.streamId,
            avatarUrl = messageDb.avatarUrl,
            content = messageDb.content,
            emojis = EmojiMapper.emojisDbToEmojisList(messageDb.emojis) as MutableList<EmojiWithCount>,
            timestamp = messageDb.timestamp
        )
    }

    fun messageDtoToMessage(messageDto: MessageDto): Message {
        return Message(
            id = messageDto.id,
            userId = messageDto.userId,
            userFullName = messageDto.userFullName,
            topicName = messageDto.topicName,
            streamId = messageDto.streamId,
            avatarUrl = messageDto.avatarUrl,
            content = messageDto.content,
            emojis = getEmojisWithCountList(messageDto.reactions) as? MutableList<EmojiWithCount>
                ?: mutableListOf(),
            timestamp = messageDto.timestamp
        )
    }

    /**
     * Преобразует список реакций всего сообщения в список эмоджи.
     * Для каждой реакции подсчитывает ее количество в сообщении.
     * В полученном списке находит и помечает эмоджи, отмеченные текущим пользователем.
     *
     * @param reactions список реакций
     * @return список эмоджи с количеством вхождений в сообщение
     */
    private fun getEmojisWithCountList(reactions: List<ReactionDto>): List<EmojiWithCount> {
        return reactions
            .groupBy { reaction -> reaction.emojiCode }
            .map { emoji -> EmojiWithCount(emoji.key, emoji.value.size) }
            .map { emojiWithCount ->
                val selfReaction = reactions.firstOrNull { reaction ->
                    reaction.userId == SELF_USER_ID && reaction.emojiCode == emojiWithCount.code
                }
                if (selfReaction != null) emojiWithCount.selectedByCurrentUser = true
                emojiWithCount
            }
    }

}
