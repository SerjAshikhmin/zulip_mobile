package ru.tinkoff.android.homework_2.data

import ru.tinkoff.android.homework_2.model.EmojiWithCount
import ru.tinkoff.android.homework_2.model.Message
import ru.tinkoff.android.homework_2.model.Reaction
import java.time.LocalDate
import java.time.LocalDateTime

fun getEmojisForMessage(messageId: Long): List<EmojiWithCount> {
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
        1,
        2,
        "Не следует, однако, забывать о том, что социально-экономическое развитие способствует подготовке и реализации ключевых компонентов планируемого обновления.",
        listOf(
            Reaction(1, "👍"),
            Reaction(2, "👍"),
            Reaction(2, "😌"),
            Reaction(1, "😎"),
            Reaction(2, "😛"),
            Reaction(1, "😏"),
            Reaction(3, "👍"),
            Reaction(4, "👍"),
            Reaction(5, "👍"),
            Reaction(6, "👍"),
            Reaction(7, "👍"),
            Reaction(8, "👍"),
            Reaction(9, "👍"),
        ),
        LocalDateTime.now().minusDays(1)
    ),
    Message(
        2,
        1,
        "Like the technical community as a whole, the Zulip team and community is made up of a mixture of professionals and volunteers from all over the world, working on every aspect of the mission, including mentorship, teaching, and connecting people.\n" +
                "\n" +
                "Diversity is one of our huge strengths, but it can also lead to communication issues and unhappiness. To that end, we have a few ground rules that we ask people to adhere to. This code applies equally to founders, mentors, and those seeking help and guidance.\n" +
                "\n" +
                "This isn’t an exhaustive list of things that you can’t do. Rather, take it in the spirit in which it’s intended — a guide to make it easier to enrich all of us and the technical communities in which we participate.",
        listOf(
            //Reaction(6, "👍"),
            //Reaction(7, "👍"),
            Reaction(1, "😏")
        ),
        LocalDateTime.now().minusDays(1)
    ),
    LocalDate.now(),
    Message(
        3,
        2,
        "Равным образом реализация намеченного плана развития напрямую зависит от дальнейших направлений развитая системы массового участия.",
        listOf(),
        LocalDateTime.now()
    )
)



