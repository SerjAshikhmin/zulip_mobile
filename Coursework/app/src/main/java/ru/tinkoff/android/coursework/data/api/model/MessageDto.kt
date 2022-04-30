package ru.tinkoff.android.coursework.data.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.tinkoff.android.coursework.data.db.model.Message

@Serializable
internal data class MessageDto (

    @SerialName("id")
    val id: Long,

    @SerialName("sender_id")
    val userId: Long,

    @SerialName("sender_full_name")
    val userFullName: String,

    @SerialName("subject")
    val topicName: String,

    @SerialName("avatar_url")
    val avatarUrl: String?,

    @SerialName("content")
    val content: String,

    @SerialName("reactions")
    val reactions: List<ReactionDto>,

    @SerialName("timestamp")
    val timestamp: Long
) {

    fun toMessageDb(): Message {
        return Message(
            id = id,
            userId = userId,
            userFullName = userFullName,
            topicName = topicName,
            avatarUrl = avatarUrl,
            content = content,
            emojis = getEmojisWithCountList(reactions),
            timestamp = timestamp
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
    private fun getEmojisWithCountList(reactions: List<ReactionDto>): List<EmojiWithCountDto> {
        return reactions
            .groupBy { reaction -> reaction.emojiCode }
            .map { emoji -> EmojiWithCountDto(emoji.key, emoji.value.size) }
            .map { emojiWithCount ->
                val selfReaction = reactions.firstOrNull { reaction ->
                    reaction.userId == SELF_USER_ID && reaction.emojiCode == emojiWithCount.code
                }
                if (selfReaction != null) emojiWithCount.selectedByCurrentUser = true
                emojiWithCount
            }
    }

}

internal fun List<MessageDto>.toMessageDbList(): List<Message> = map { messageDto -> messageDto.toMessageDb()}
