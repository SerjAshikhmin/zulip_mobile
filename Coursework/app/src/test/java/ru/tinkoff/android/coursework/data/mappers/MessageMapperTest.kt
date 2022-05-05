package ru.tinkoff.android.coursework.data.mappers

import org.junit.Test
import org.junit.Assert.assertEquals
import ru.tinkoff.android.coursework.data.api.model.MessageDto
import ru.tinkoff.android.coursework.data.api.model.ReactionDto
import ru.tinkoff.android.coursework.data.db.model.EmojiWithCountDb
import ru.tinkoff.android.coursework.data.db.model.MessageDb
import ru.tinkoff.android.coursework.domain.model.EmojiWithCount
import ru.tinkoff.android.coursework.domain.model.Message

internal class MessageMapperTest {

    @Test
    fun `messageDbToMessage by default returns message`() {
        val messageDb = createMessageDb(
            id = 1L,
            userId = 1L,
            userFullName = "Test User",
            topicName = "test topic",
            avatarUrl = "https://testUrl",
            content = "test content",
            emojis = getEmojisDbTestList(),
            timestamp = 1650095396L
        )

        val message = MessageMapper.messageDbToMessage(messageDb)

        assertEquals(1L, message.id)
        assertEquals(1L, message.userId)
        assertEquals("Test User", message.userFullName)
        assertEquals("test topic", message.topicName)
        assertEquals("https://testUrl", message.avatarUrl)
        assertEquals("test content", message.content)
        assertEquals(getEmojisTestList(), message.emojis)
        assertEquals(1650095396L, message.timestamp)
    }

    @Test
    fun `messagesDbToMessagesList by default returns messages list`() {
        val messagesDb = listOf(
            createMessageDb(
                id = 1L,
                userId = 1L,
                userFullName = "Test User",
                topicName = "test topic",
                avatarUrl = "https://testUrl",
                content = "test content",
                emojis = getEmojisDbTestList(),
                timestamp = 1650095396L
            ),
            createMessageDb(
                id = 2L,
                userId = 2L,
                userFullName = "Second Test User",
                topicName = "second test topic",
                avatarUrl = "https://secondTestUrl",
                content = "second test content",
                emojis = getEmojisDbTestList(),
                timestamp = 1650095398L
            )
        )

        val messages = MessageMapper.messagesDbToMessagesList(messagesDb)

        assertEquals(1L, messages[0].id)
        assertEquals(1L, messages[0].userId)
        assertEquals("Test User", messages[0].userFullName)
        assertEquals("test topic", messages[0].topicName)
        assertEquals("https://testUrl", messages[0].avatarUrl)
        assertEquals("test content", messages[0].content)
        assertEquals(getEmojisTestList(), messages[0].emojis)
        assertEquals(1650095396L, messages[0].timestamp)

        assertEquals(2L, messages[1].id)
        assertEquals(2L, messages[1].userId)
        assertEquals("Second Test User", messages[1].userFullName)
        assertEquals("second test topic", messages[1].topicName)
        assertEquals("https://secondTestUrl", messages[1].avatarUrl)
        assertEquals("second test content", messages[1].content)
        assertEquals(getEmojisTestList(), messages[1].emojis)
        assertEquals(1650095398L, messages[1].timestamp)
    }

    @Test
    fun `messageDtoToMessage by default returns message`() {
        val messageDto = createMessageDto(
            id = 1L,
            userId = 1L,
            userFullName = "Test User",
            topicName = "test topic",
            avatarUrl = "https://testUrl",
            content = "test content",
            emojis = getReactionDtoTestList(),
            timestamp = 1650095396L
        )

        val message = MessageMapper.messageDtoToMessage(messageDto)

        assertEquals(1L, message.id)
        assertEquals(1L, message.userId)
        assertEquals("Test User", message.userFullName)
        assertEquals("test topic", message.topicName)
        assertEquals("https://testUrl", message.avatarUrl)
        assertEquals("test content", message.content)
        assertEquals(getEmojisTestList(), message.emojis)
        assertEquals(1650095396L, message.timestamp)
    }

    @Test
    fun `messageToMessageDb by default returns message`() {
        val message = createMessage(
            id = 1L,
            userId = 1L,
            userFullName = "Test User",
            topicName = "test topic",
            avatarUrl = "https://testUrl",
            content = "test content",
            emojis = getEmojisTestList(),
            timestamp = 1650095396L
        )

        val messageDb = MessageMapper.messageToMessageDb(message)

        assertEquals(1L, messageDb.id)
        assertEquals(1L, messageDb.userId)
        assertEquals("Test User", messageDb.userFullName)
        assertEquals("test topic", messageDb.topicName)
        assertEquals("https://testUrl", messageDb.avatarUrl)
        assertEquals("test content", messageDb.content)
        assertEquals(getEmojisDbTestList(), messageDb.emojis)
        assertEquals(1650095396L, messageDb.timestamp)
    }

    private fun createMessage(
        id: Long,
        userId: Long,
        userFullName: String,
        topicName: String,
        avatarUrl: String,
        content: String,
        emojis: List<EmojiWithCount>,
        timestamp: Long
    ): Message {
        return Message(
            id = id,
            userId = userId,
            userFullName = userFullName,
            topicName = topicName,
            avatarUrl = avatarUrl,
            content = content,
            emojis = emojis.toMutableList(),
            timestamp = timestamp
        )
    }

    private fun createMessageDto(
        id: Long,
        userId: Long,
        userFullName: String,
        topicName: String,
        avatarUrl: String,
        content: String,
        emojis: List<ReactionDto>,
        timestamp: Long
    ): MessageDto {
        return MessageDto(
            id = id,
            userId = userId,
            userFullName = userFullName,
            topicName = topicName,
            avatarUrl = avatarUrl,
            content = content,
            reactions = emojis,
            timestamp = timestamp
        )
    }

    private fun createMessageDb(
        id: Long = 0,
        userId: Long,
        userFullName: String,
        topicName: String,
        avatarUrl: String?,
        content: String,
        emojis: List<EmojiWithCountDb>,
        timestamp: Long
    ): MessageDb {
        return MessageDb(
            id = id,
            userId = userId,
            userFullName = userFullName,
            topicName = topicName,
            avatarUrl = avatarUrl,
            content = content,
            emojis = emojis,
            timestamp = timestamp
        )
    }

    private fun getEmojisDbTestList(): List<EmojiWithCountDb> {
        return listOf(
            EmojiWithCountDb(
                code = "1f636",
                count = 1,
                selectedByCurrentUser = false
            ),
            EmojiWithCountDb(
                code = "1f60e",
                count = 2,
                selectedByCurrentUser = false
            )
        )
    }

    private fun getEmojisTestList(): List<EmojiWithCount> {
        return listOf(
            EmojiWithCount(
                code = "1f636",
                count = 1,
                selectedByCurrentUser = false
            ),
            EmojiWithCount(
                code = "1f60e",
                count = 2,
                selectedByCurrentUser = false
            )
        )
    }

    private fun getReactionDtoTestList(): List<ReactionDto> {
        return listOf(
            ReactionDto(
                userId = 1L,
                emojiName = "first test emoji",
                emojiCode = "1f636"
            ),
            ReactionDto(
                userId = 1L,
                emojiName = "second test emoji",
                emojiCode = "1f60e"
            ),
            ReactionDto(
                userId = 1L,
                emojiName = "second test emoji",
                emojiCode = "1f60e"
            )
        )
    }

}
