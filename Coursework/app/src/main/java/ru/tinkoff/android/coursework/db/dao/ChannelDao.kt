package ru.tinkoff.android.coursework.db.dao

import androidx.room.*
import io.reactivex.Single
import ru.tinkoff.android.coursework.db.model.Channel

@Dao
internal interface ChannelDao {

    @Query("SELECT * FROM channel")
    fun getAll(): Single<List<Channel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(channel: Channel): Single<Long>

}
