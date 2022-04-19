package ru.tinkoff.android.coursework.data.db.dao

import androidx.room.*
import io.reactivex.Single
import ru.tinkoff.android.coursework.data.db.model.Stream

@Dao
internal interface StreamDao {

    @Query("SELECT * FROM stream")
    fun getAll(): Single<List<Stream>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(stream: Stream): Single<Long>

}
