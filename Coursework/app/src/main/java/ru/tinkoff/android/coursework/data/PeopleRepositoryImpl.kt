package ru.tinkoff.android.coursework.data

import android.content.Context
import android.os.Bundle
import android.util.Log
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.data.api.NetworkService
import ru.tinkoff.android.coursework.data.api.model.UserDto
import ru.tinkoff.android.coursework.data.api.model.response.UserPresenceResponse
import ru.tinkoff.android.coursework.data.db.AppDatabase
import ru.tinkoff.android.coursework.data.db.model.User
import ru.tinkoff.android.coursework.data.db.model.toUsersDtoList
import ru.tinkoff.android.coursework.presentation.screens.PeopleFragment
import ru.tinkoff.android.coursework.presentation.screens.ProfileFragment

internal class PeopleRepositoryImpl(private val applicationContext: Context) : PeopleRepository {

    private var db: AppDatabase? = AppDatabase.getAppDatabase(applicationContext)

    override fun loadUsersFromDb(): Observable<List<UserDto>> {
        return db?.userDao()?.getAll()
            ?.map { it.toUsersDtoList() }
            ?.doOnError {
                Log.e(TAG, applicationContext.resources.getString(R.string.loading_users_from_db_error_text), it)
            }
            ?.toObservable()
            ?: Observable.just(listOf())
    }

    override fun loadUsersFromApi(): Observable<List<UserDto>> {
        return NetworkService.getZulipJsonApi().getAllUsers()
            .map { it.members }
            .flatMapObservable  { Observable.fromIterable(it)  }
            .flatMapSingle { getUserPresence(it) }
            .doOnError {
                Log.e(TAG, applicationContext.resources.getString(R.string.loading_users_from_api_error_text), it)
            }
            .toList()
            .toObservable()
    }

    override fun loadUserFromDb(userId: Long): Observable<UserDto>? {
        return db?.userDao()?.getById(userId)
            ?.map { it.toUserDto() }
            ?.doOnError {
                Log.e(TAG, applicationContext.resources.getString(R.string.loading_users_from_db_error_text), it)
            }
            ?.toObservable()
    }

    override fun loadOwnUserFromApi(): Observable<UserDto> {
        return NetworkService.getZulipJsonApi().getOwnUser()
            .flatMap { user -> getUserPresence(user) }
            .doOnError {
                Log.e(TAG, applicationContext.resources.getString(R.string.user_not_found_error_text), it)
            }
            .toObservable()
    }

    override fun createUserFromBundle(bundle: Bundle): Single<UserDto> {
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
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                user.presence = it.presence.aggregated?.status ?: PeopleFragment.NOT_FOUND_PRESENCE_KEY
                saveUserToDb(user.toUserDb())
            }
            .onErrorReturn {
                Log.e(TAG, applicationContext.resources.getString(R.string.user_not_found_error_text), it)
                user.presence = PeopleFragment.NOT_FOUND_PRESENCE_KEY
                UserPresenceResponse()
            }
            .map { user }
    }

    private fun saveUserToDb(user: User) {
        db?.userDao()?.save(user)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.onErrorReturn {
                Log.e(TAG, applicationContext.resources.getString(R.string.saving_user_to_db_error_text), it)
                DEFAULT_USER_ID
            }?.subscribe()
    }

    companion object {

        private const val TAG = "PeopleRepository"
        private const val DEFAULT_USER_ID = 0L
    }

}
