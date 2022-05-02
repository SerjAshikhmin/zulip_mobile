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

    @Query("SELECT * FROM message WHERE topic_name == :topic ORDER BY id ASC")
    fun getAllByTopic(topic: String): Single<List<MessageDb>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAll(messages: List<MessageDb>): Single<List<Long>>

    @Query("DELETE FROM message WHERE topic_name == :topic")
    fun removeAllFromTopic(topic: String): Completable

}
