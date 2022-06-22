package ru.tinkoff.android.coursework.data

import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.tinkoff.android.coursework.data.api.ZulipJsonApi
import ru.tinkoff.android.coursework.domain.model.Result
import ru.tinkoff.android.coursework.domain.model.LoggedInUser

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

internal class LoginRepositoryImpl {

    lateinit var zulipJsonApi: ZulipJsonApi

    // in-memory cache of the loggedInUser object
    var user: LoggedInUser? = null
        private set

    //var disposables: CompositeDisposable = CompositeDisposable()
    val loginResult = MutableLiveData<Result<LoggedInUser>>()

    suspend fun login(username: String, password: String) {
        withContext(Dispatchers.IO) {
            try {
                val loginResponse = zulipJsonApi.fetchApiKey(username, password)
                val loggedInUser = LoggedInUser(
                    userName = loginResponse.email,
                    apiKey = loginResponse.apiKey
                )
                setLoggedInUser(loggedInUser)
                loginResult.postValue(Result.Success(loggedInUser))
            } catch (e: Exception) {
                loginResult.postValue(Result.Error(e))
            }
        }
    }

    // Rx implementation
    /*fun login(username: String, password: String) {
        zulipJsonApi.fetchApiKey(username, password)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    val loggedInUser = LoggedInUser(
                        userName = it.email,
                        apiKey = it.apiKey
                    )
                    setLoggedInUser(loggedInUser)
                    loginResult.value = Result.Success(loggedInUser)
                },
                onError = {
                    loginResult.value = Result.Error(it as Exception)
                }
            )
            .addTo(disposables)
    }*/

    private fun setLoggedInUser(loggedInUser: LoggedInUser) {
        this.user = loggedInUser
    }

}
