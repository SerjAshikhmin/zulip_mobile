package ru.tinkoff.android.coursework.domain.people

import io.reactivex.Observable
import io.reactivex.Single
import ru.tinkoff.android.coursework.data.PeopleRepository
import ru.tinkoff.android.coursework.data.api.model.UserDto

internal class PeopleUseCases (
    private val peopleRepository: PeopleRepository
) {

    fun loadUsers(): Observable<List<UserDto>> {
        return Observable.merge(
            peopleRepository.loadUsersFromDb().toObservable(),
            peopleRepository.loadUsersFromApi().toObservable()
        )
    }

}
