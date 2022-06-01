package ru.tinkoff.android.coursework.data.db.dao

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Single
import ru.tinkoff.android.coursework.data.db.model.StreamDb

@Dao
internal interface StreamDao {

    @Query("SELECT * FROM stream")
    fun getAll(): Single<List<StreamDb>>

    @Query("SELECT * FROM stream WHERE is_subscribed = 1")
    fun getAllSubscribed(): Single<List<StreamDb>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun saveAllIgnoreConflicts(streams: List<StreamDb>): Single<List<Long>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAllReplaceConflicts(streams: List<StreamDb>): Single<List<Long>>

    @Query("DELETE FROM stream WHERE is_subscribed = :isSubscribedStreams")
    fun deleteAllBySubscribedSign(isSubscribedStreams: Boolean): Completable

}
