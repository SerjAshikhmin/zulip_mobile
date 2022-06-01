package ru.tinkoff.android.coursework.data

import android.os.Bundle
import android.util.Log
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.tinkoff.android.coursework.data.api.ZulipJsonApi
import ru.tinkoff.android.coursework.data.api.model.UserDto
import ru.tinkoff.android.coursework.data.api.model.response.UserPresenceResponse
import ru.tinkoff.android.coursework.data.db.AppDatabase
import ru.tinkoff.android.coursework.data.mappers.UserMapper
import ru.tinkoff.android.coursework.domain.interfaces.PeopleRepository
import ru.tinkoff.android.coursework.domain.model.User
import ru.tinkoff.android.coursework.presentation.screens.PeopleFragment
import ru.tinkoff.android.coursework.presentation.screens.ProfileFragment
import javax.inject.Inject

internal class PeopleRepositoryImpl @Inject constructor(
    private val zulipJsonApi: ZulipJsonApi,
    private val db: AppDatabase
) : PeopleRepository {

    override fun loadUsersFromDb(): Single<List<User>> {
        return db.userDao().getAll()
            .map { UserMapper.usersDbToUsersList(it) }
            .doOnError {
                Log.e(TAG, "Loading users from db error", it)
            }
    }

    override fun loadUsersFromApi(): Single<List<User>> {
        return zulipJsonApi.getAllUsers()
            .flattenAsObservable { it.members }
            .flatMapSingle { getUserPresence(it) }
            .doOnError {
                Log.e(TAG, "Loading users from api error", it)
            }
            .toList()
    }

    override fun loadUserFromDb(userId: Long): Single<User> {
        return db.userDao().getById(userId)
            .map { UserMapper.userDbToUser(it) }
            .doOnError {
                Log.e(TAG, "Loading users from db error", it)
            }
    }

    override fun loadOwnUserFromApi(): Single<User> {
        return zulipJsonApi.getOwnUser()
            .flatMap { user -> getUserPresence(user) }
            .doOnError {
                Log.e(TAG, "User not found", it)
            }
    }

    override fun createUserFromBundle(bundle: Bundle): Single<User> {
        return Single.just(User(
            userId = bundle.getLong(ProfileFragment.USER_ID_KEY),
            fullName = bundle.getString(ProfileFragment.USERNAME_KEY),
            email = bundle.getString(ProfileFragment.EMAIL_KEY),
            avatarUrl = bundle.getString(ProfileFragment.AVATAR_KEY),
            presence = bundle.getString(ProfileFragment.USER_PRESENCE_KEY)
        ))
    }

    override fun saveUsersToDb(users: List<User>) {
        db.userDao().saveAll(UserMapper.usersToUsersDbList(users))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorReturn {
                Log.e(TAG, "Saving users to db error", it)
                emptyList()
            }.subscribe()
    }

    private fun getUserPresence(user: UserDto): Single<User> {
        return zulipJsonApi.getUserPresence(userIdOrEmail = user.userId.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                user.presence = it.presence.aggregated?.status ?: PeopleFragment.NOT_FOUND_PRESENCE_KEY
            }
            .onErrorReturn {
                Log.e(TAG, "User not found", it)
                user.presence = PeopleFragment.NOT_FOUND_PRESENCE_KEY
                UserPresenceResponse()
            }
            .map { UserMapper.userDtoToUser(user) }
    }

    companion object {

        private const val TAG = "PeopleRepository"
    }

}
