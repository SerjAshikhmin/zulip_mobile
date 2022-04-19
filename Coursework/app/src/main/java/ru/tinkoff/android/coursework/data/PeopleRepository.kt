package ru.tinkoff.android.coursework.data

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Observable
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
import ru.tinkoff.android.coursework.presentation.screens.ProfileFragment
import ru.tinkoff.android.coursework.utils.showSnackBarWithRetryAction

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
            //.map { users -> users.map { user -> getUserPresence(user) } }
            .doOnSuccess { users -> users.forEach { user -> getUserPresence(user) } }
    }

    fun loadUserFromDb(userId: Long): Single<UserDto>? {
        return db?.userDao()?.getById(userId)
            ?.map { it.toUserDto() }
            ?.doOnError {
                Log.e(TAG, applicationContext.resources.getString(R.string.loading_users_from_db_error_text), it)
            }
    }

    fun loadOwnUserFromApi(): Single<UserDto> {
        return NetworkService.getZulipJsonApi().getOwnUser()
            .flatMap { user -> getUserPresence(user) }
            //.doOnSuccess { user -> getUserPresence(user) }
            .doOnError {
                Log.e(TAG, applicationContext.resources.getString(R.string.user_not_found_error_text), it)
            }
    }

    fun createUserFromBundle(bundle: Bundle): Single<UserDto> {
        return Single.just(UserDto(
            userId = bundle.getLong(ProfileFragment.USER_ID_KEY),
            fullName = bundle.getString(ProfileFragment.USERNAME_KEY),
            email = bundle.getString(ProfileFragment.EMAIL_KEY),
            avatarUrl = bundle.getString(ProfileFragment.AVATAR_KEY),
            presence = bundle.getString(ProfileFragment.USER_PRESENCE_KEY)
        ))
    }

    private fun getUserPresence(user: UserDto): Single<UserDto> {
        return NetworkService.getZulipJsonApi().getUserPresence(userIdOrEmail = user.userId.toString())
            .doOnSuccess { user.presence = it.presence.aggregated?.status ?: PeopleFragment.NOT_FOUND_PRESENCE_KEY
                saveUserToDb(user.toUserDb()) }
            .map { user }
            /*.subscribeBy(
                onSuccess = {
                    user.presence = it.presence.aggregated?.status ?: PeopleFragment.NOT_FOUND_PRESENCE_KEY
                    saveUserToDb(user.toUserDb())
                },
                onError = {
                    Log.e(TAG, applicationContext.resources.getString(R.string.user_not_found_error_text), it)
                    user.presence = PeopleFragment.NOT_FOUND_PRESENCE_KEY
                }
            )
            .addTo(compositeDisposable)*/
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
