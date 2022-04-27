package ru.tinkoff.android.coursework.domain.profile

import android.os.Bundle
import io.reactivex.Observable
import io.reactivex.Single
import ru.tinkoff.android.coursework.data.PeopleRepository
import ru.tinkoff.android.coursework.data.api.model.SELF_USER_ID
import ru.tinkoff.android.coursework.data.api.model.UserDto

internal class ProfileUseCases (
    private val peopleRepository: PeopleRepository
) {

    fun loadOwnUser(): Observable<UserDto> {
        return Observable.merge(
            peopleRepository.loadUserFromDb(SELF_USER_ID),
            peopleRepository.loadOwnUserFromApi()
        )
    }

    fun createUserFromBundle(bundle: Bundle): Single<UserDto> {
        return peopleRepository.createUserFromBundle(bundle)
    }

}
