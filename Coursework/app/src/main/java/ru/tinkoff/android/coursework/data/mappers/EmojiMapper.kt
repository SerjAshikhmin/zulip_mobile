package ru.tinkoff.android.coursework.data.mappers

import ru.tinkoff.android.coursework.data.db.model.EmojiWithCountDb
import ru.tinkoff.android.coursework.domain.model.EmojiWithCount

internal object EmojiMapper {

    fun emojisToEmojisDbList(emojis: List<EmojiWithCount>): List<EmojiWithCountDb> =
        emojis.map { emoji -> emojiToEmojiDb(emoji) }

    fun emojisDbToEmojisList(emojisDb: List<EmojiWithCountDb>): List<EmojiWithCount> =
        emojisDb.map { emoji -> emojiDbToEmoji(emoji) }

    private fun emojiToEmojiDb(emoji: EmojiWithCount): EmojiWithCountDb {
        return EmojiWithCountDb(
            code = emoji.code,
            count = emoji.count,
            selectedByCurrentUser = emoji.selectedByCurrentUser
        )
    }

    private fun emojiDbToEmoji(emojiDb: EmojiWithCountDb): EmojiWithCount {
        return EmojiWithCount(
            code = emojiDb.code,
            count = emojiDb.count,
            selectedByCurrentUser = emojiDb.selectedByCurrentUser
        )
    }

}
