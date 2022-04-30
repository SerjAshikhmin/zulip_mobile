package ru.tinkoff.android.coursework.data

import android.os.Bundle
import io.reactivex.Observable
import io.reactivex.Single
import ru.tinkoff.android.coursework.data.api.model.UserDto
import ru.tinkoff.android.coursework.data.db.model.User

internal interface PeopleRepository {

    fun loadUsersFromDb(): Observable<List<UserDto>>
    fun loadUsersFromApi(): Observable<List<UserDto>>
    fun loadUserFromDb(userId: Long): Observable<UserDto>?
    fun loadOwnUserFromApi(): Observable<UserDto>
    fun createUserFromBundle(bundle: Bundle): Single<UserDto>

}
