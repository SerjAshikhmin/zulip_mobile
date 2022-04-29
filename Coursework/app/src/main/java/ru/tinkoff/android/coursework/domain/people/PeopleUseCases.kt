package ru.tinkoff.android.coursework.domain.people

import io.reactivex.Observable
import ru.tinkoff.android.coursework.data.PeopleRepository
import ru.tinkoff.android.coursework.data.api.model.UserDto
import ru.tinkoff.android.coursework.data.api.model.toUsersDbList

internal class PeopleUseCases (
    private val peopleRepository: PeopleRepository
) {

    fun loadUsers(): Observable<List<UserDto>> {
        return Observable.merge(
            peopleRepository.loadUsersFromDb().toObservable(),
            peopleRepository.loadUsersFromApi()
                .doOnSuccess {
                    peopleRepository.saveUsersToDb(it.toUsersDbList())
                }
                .toObservable()
        )
    }

}
