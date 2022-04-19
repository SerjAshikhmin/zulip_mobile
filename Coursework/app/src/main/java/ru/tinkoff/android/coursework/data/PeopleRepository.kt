package ru.tinkoff.android.coursework.data

import android.content.Context
import android.util.Log
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.data.api.NetworkService
import ru.tinkoff.android.coursework.data.api.model.UserDto
import ru.tinkoff.android.coursework.data.db.AppDatabase
import ru.tinkoff.android.coursework.data.db.model.User
import ru.tinkoff.android.coursework.data.db.model.toUsersDtoList
import ru.tinkoff.android.coursework.presentation.screens.PeopleFragment

internal class PeopleRepository(private val applicationContext: Context) {

    private var db: AppDatabase? = AppDatabase.getAppDatabase(applicationContext)
    var compositeDisposable: CompositeDisposable = CompositeDisposable()

    fun loadUsersFromDb(): Single<List<UserDto>> {
        return db?.userDao()?.getAll()
            ?.map { it.toUsersDtoList() }
            ?.doOnError {
                Log.e(TAG, applicationContext.resources.getString(R.string.loading_users_from_db_error_text), it)
            }
            ?: Single.just(listOf())
    }

    fun loadUsersFromApi(): Single<List<UserDto>> {
        return NetworkService.getZulipJsonApi().getAllUsers()
            .map { it.members }
            .doOnSuccess { users -> users.forEach { user -> getUserPresence(user) } }
    }

    private fun getUserPresence(user: UserDto) {
        NetworkService.getZulipJsonApi().getUserPresence(userIdOrEmail = user.userId.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    user.presence = it.presence.aggregated?.status ?: PeopleFragment.NOT_FOUND_PRESENCE_KEY
                    saveUserToDb(user.toUserDb())
                },
                onError = {
                    user.presence = PeopleFragment.NOT_FOUND_PRESENCE_KEY
                }
            )
            .addTo(compositeDisposable)
    }

    private fun saveUserToDb(user: User) {
        db?.userDao()?.save(user)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribeBy(
                onError = {
                    Log.e(TAG, applicationContext.resources.getString(R.string.saving_user_to_db_error_text), it)
                }
            )
            ?.addTo(compositeDisposable)
    }

    companion object {

        private const val TAG = "PeopleRepository"
    }

}
