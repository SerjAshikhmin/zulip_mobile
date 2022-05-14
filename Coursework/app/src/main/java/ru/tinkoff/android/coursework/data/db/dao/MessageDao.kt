package ru.tinkoff.android.coursework.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Single
import ru.tinkoff.android.coursework.data.db.model.MessageDb

@Dao
internal interface MessageDao {

    @Query("SELECT * FROM message WHERE topic_name = :topicName ORDER BY id ASC")
    fun getAllByTopic(topicName: String): Single<List<MessageDb>>

    @Query("SELECT * FROM message WHERE message.stream_id = " +
            "(SELECT streamId FROM stream WHERE name = :streamName) ORDER BY id ASC")
    fun getAllByStream(streamName: String): Single<List<MessageDb>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAll(messages: List<MessageDb>): Single<List<Long>>

    @Query("DELETE FROM message WHERE topic_name == :topic")
    fun removeAllFromTopic(topic: String): Completable

}
