package ru.tinkoff.android.coursework.domain.people

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.tinkoff.android.coursework.domain.interfaces.PeopleRepository
import ru.tinkoff.android.coursework.domain.model.User

internal class PeopleInteractor (
    private val peopleRepository: PeopleRepository
) {

    fun loadUsers(): Observable<List<User>> {
        return Single.merge(
            peopleRepository.loadUsersFromDb(),
            peopleRepository.loadUsersFromApi()
                .doOnSuccess {
                    peopleRepository.saveUsersToDb(it)
                }
        ).toObservable()
            // задержка для отображения шиммера
            //.delay(1000L, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
    }

    fun updateUsers(): Observable<List<User>> {
        return peopleRepository.loadUsersFromApi()
            .doOnSuccess {
                peopleRepository.saveUsersToDb(it)
            }
            .toObservable()
            .subscribeOn(Schedulers.io())
    }

}
