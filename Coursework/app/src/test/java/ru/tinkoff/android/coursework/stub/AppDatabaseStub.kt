package ru.tinkoff.android.coursework.stub

import androidx.room.DatabaseConfiguration
import androidx.room.InvalidationTracker
import androidx.sqlite.db.SupportSQLiteOpenHelper
import io.reactivex.Completable
import io.reactivex.Single
import org.mockito.Mockito
import org.mockito.Mockito.anyList
import org.mockito.Mockito.mock
import ru.tinkoff.android.coursework.data.api.model.TopicDto
import ru.tinkoff.android.coursework.data.db.AppDatabase
import ru.tinkoff.android.coursework.data.db.dao.MessageDao
import ru.tinkoff.android.coursework.data.db.dao.StreamDao
import ru.tinkoff.android.coursework.data.db.dao.UserDao
import ru.tinkoff.android.coursework.data.db.model.StreamDb

internal class AppDatabaseStub : AppDatabase() {

    override fun userDao(): UserDao {
        return mock(UserDao::class.java)
    }

    override fun messageDao(): MessageDao {
        return mock(MessageDao::class.java)
    }

    override fun streamDao(): StreamDao {
        val streamDaoMock = mock(StreamDao::class.java)
        mockStreamDaoGetAllCall(streamDaoMock)
        mockStreamDaoSaveAllCall(streamDaoMock)
        mockStreamDaoDeleteAllBySubscribedSign(streamDaoMock)
        return streamDaoMock
    }

    override fun createOpenHelper(config: DatabaseConfiguration?): SupportSQLiteOpenHelper {
        return mock(SupportSQLiteOpenHelper::class.java)
    }

    override fun createInvalidationTracker(): InvalidationTracker {
        return mock(InvalidationTracker::class.java)
    }

    override fun clearAllTables() { }

    private fun mockStreamDaoSaveAllCall(streamDaoMock: StreamDao) {
        Mockito.`when`(streamDaoMock.saveAllReplaceConflicts(anyList()))
            .thenReturn(Single.just(emptyList()))
    }

    private fun mockStreamDaoGetAllCall(streamDaoMock: StreamDao) {
        Mockito.`when`(streamDaoMock.getAllSubscribed()).thenReturn(
            Single.just(
                listOf(
                    StreamDb(
                        streamId = 1L,
                        name = "first test stream",
                        topics = listOf(
                            TopicDto("first test topic")
                        ),
                        isSubscribed = true
                    ),
                    StreamDb(
                        streamId = 2L,
                        name = "second test stream",
                        topics = listOf(
                            TopicDto("second test topic"),
                            TopicDto("third test topic")
                        ),
                        isSubscribed = true
                    )
                )
            )
        )
    }

    private fun mockStreamDaoDeleteAllBySubscribedSign(streamDaoMock: StreamDao) {
        Mockito.`when`(streamDaoMock.deleteAllBySubscribedSign(isSubscribedStreams = true))
            .thenReturn(Completable.complete())
    }

}
