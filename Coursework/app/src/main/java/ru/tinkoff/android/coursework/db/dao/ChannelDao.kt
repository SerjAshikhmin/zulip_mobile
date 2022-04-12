package ru.tinkoff.android.coursework.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.tinkoff.android.coursework.db.model.Channel

@Dao
internal interface ChannelDao {

    @Query("SELECT * FROM channel")
    fun getAll(): List<Channel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAll(channels: List<Channel>): Int
}
