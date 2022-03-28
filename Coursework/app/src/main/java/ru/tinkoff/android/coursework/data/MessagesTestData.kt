package ru.tinkoff.android.coursework.data

import ru.tinkoff.android.coursework.model.EmojiWithCount
import ru.tinkoff.android.coursework.model.Message
import ru.tinkoff.android.coursework.model.Reaction
import java.time.LocalDate
import java.time.LocalDateTime

// поиск списка эмоджи для сообщения
// находит сообщение по id, для каждой реакции подсчитывает ее количество в сообщении
// в полученном списке находит и помечает реакции, отмеченные текущим пользователем
internal fun getEmojisForMessage(messageId: Long): List<EmojiWithCount> {
    val message = messages.first { it is Message && it.id == messageId }
    return if (message is Message) {
        val emojiList = mutableListOf<EmojiWithCount>()
        message.reactions
            .groupBy { reaction -> reaction.code }
            .map { emoji -> emoji.key to emoji.value.size }
            .mapTo(emojiList) { emoji -> EmojiWithCount(emoji.first, emoji.second)}

        emojiList.forEach { emojiWithCount ->
            val selfReaction = message.reactions.firstOrNull { reaction ->
                reaction.userId == SELF_USER_ID && reaction.code == emojiWithCount.code
            }
            if (selfReaction != null) emojiWithCount.selectedByCurrentUser = true
        }
        return emojiList
    } else {
        listOf()
    }
}

internal var messages = mutableListOf(
    LocalDate.now().minusDays(1),
    Message(
        id = 1,
        userId = 2,
        content = "Не следует, однако, забывать о том, что социально-экономическое развитие " +
            "способствует подготовке и реализации ключевых компонентов планируемого обновления.",
        reactions = listOf(
            Reaction(userId = 1, code = "👍"),
            Reaction(userId = 2, code = "👍"),
            Reaction(userId = 2, code = "😌"),
            Reaction(userId = 1, code = "😎"),
            Reaction(userId = 2, code = "😛"),
            Reaction(userId = 1, code = "😏"),
            Reaction(userId = 3, code = "👍"),
            Reaction(userId = 4, code = "👍"),
            Reaction(userId = 5, code = "👍"),
            Reaction(userId = 6, code = "👍"),
            Reaction(userId = 7, code = "👍"),
            Reaction(userId = 8, code = "👍"),
            Reaction(userId = 9, code = "👍"),
        ),
        sendDateTime = LocalDateTime.now().minusDays(1)
    ),
    Message(
        id = 2,
        userId = 1,
        content = "Like the technical community as a whole, the Zulip team and community is made " +
                "up of a mixture of professionals and volunteers from all over the world, working" +
                " on every aspect of the mission, including mentorship, teaching, and connecting" +
                " people.\n\nDiversity is one of our huge strengths, but it can also lead to " +
                "communication issues and unhappiness. To that end, we have a few ground rules " +
                "that we ask people to adhere to. This code applies equally to founders, mentors," +
                " and those seeking help and guidance.\n\nThis isn't an exhaustive list of things" +
                " that you can't do. Rather, take it in the spirit in which it's intended --- a" +
                " guide to make it easier to enrich all of us and the technical communities in" +
                " which we participate.",
        reactions = listOf(
            Reaction(userId = 6, code = "👍"),
            Reaction(userId = 7, code = "👍"),
            Reaction(userId = 1, code = "😏")
        ),
        sendDateTime = LocalDateTime.now().minusDays(1)
    ),
    LocalDate.now(),
    Message(
        id = 3,
        userId = 2,
        content = "Равным образом реализация намеченного плана развития напрямую зависит от дальнейших направлений развитая системы массового участия.",
        reactions = listOf(),
        sendDateTime = LocalDateTime.now()
    )
)
