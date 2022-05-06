package ru.tinkoff.android.coursework.data

import android.os.Bundle
import io.reactivex.Single
import ru.tinkoff.android.coursework.domain.model.User

internal interface PeopleRepository {

    fun loadUsersFromDb(): Single<List<User>>

    fun loadUsersFromApi(): Single<List<User>>

    fun loadUserFromDb(userId: Long): Single<User>

    fun loadOwnUserFromApi(): Single<User>

    fun createUserFromBundle(bundle: Bundle): Single<User>

    fun saveUsersToDb(users: List<User>)

}
