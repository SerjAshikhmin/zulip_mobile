package ru.tinkoff.android.coursework.data.db.dao

import androidx.room.*
import io.reactivex.Single
import ru.tinkoff.android.coursework.data.db.model.StreamDb

@Dao
internal interface StreamDao {

    @Query("SELECT * FROM stream")
    fun getAll(): Single<List<StreamDb>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAll(streams: List<StreamDb>): Single<List<Long>>

}
