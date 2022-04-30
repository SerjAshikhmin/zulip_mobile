package ru.tinkoff.android.coursework.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Single
import ru.tinkoff.android.coursework.data.db.model.Message

@Dao
internal interface MessageDao {

    @Query("SELECT * FROM message WHERE topic_name == :topic ORDER BY timestamp ASC")
    fun getAllByTopic(topic: String): Single<List<Message>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAll(messages: List<Message>): Single<List<Long>>

    @Query("DELETE FROM message WHERE topic_name == :topic AND id NOT IN (:actualMessageIds)")
    fun removeRedundant(topic: String, actualMessageIds: List<Long>): Completable

}
