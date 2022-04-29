package ru.tinkoff.android.coursework.data

import android.os.Bundle
import io.reactivex.Single
import ru.tinkoff.android.coursework.data.api.model.UserDto

internal interface PeopleRepository {

    fun loadUsersFromDb(): Single<List<UserDto>>
    fun loadUsersFromApi(): Single<List<UserDto>>
    fun loadUserFromDb(userId: Long): Single<UserDto>
    fun loadOwnUserFromApi(): Single<UserDto>
    fun createUserFromBundle(bundle: Bundle): Single<UserDto>

}
