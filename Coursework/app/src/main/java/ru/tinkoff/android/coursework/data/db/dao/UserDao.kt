package ru.tinkoff.android.coursework.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Single
import ru.tinkoff.android.coursework.data.db.model.UserDb

@Dao
internal interface UserDao {

    @Query("SELECT * FROM user WHERE userId == :userId")
    fun getById(userId: Long): Single<UserDb>

    @Query("SELECT * FROM user")
    fun getAll(): Single<List<UserDb>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAll(users: List<UserDb>): Single<List<Long>>

}
