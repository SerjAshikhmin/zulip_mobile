package ru.tinkoff.android.coursework.domain.profile

import android.os.Bundle
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.tinkoff.android.coursework.domain.interfaces.PeopleRepository
import ru.tinkoff.android.coursework.data.api.model.SELF_USER_ID
import ru.tinkoff.android.coursework.domain.model.User

internal class ProfileInteractor (
    private val peopleRepository: PeopleRepository
) {

    fun loadOwnUser(): Observable<User> {
        return Single.merge(
            peopleRepository.loadUserFromDb(SELF_USER_ID),
            peopleRepository.loadOwnUserFromApi()
        ).toObservable()
            .subscribeOn(Schedulers.io())
    }

    fun createUserFromBundle(bundle: Bundle): Single<User> {
        return peopleRepository.createUserFromBundle(bundle)
    }

}
