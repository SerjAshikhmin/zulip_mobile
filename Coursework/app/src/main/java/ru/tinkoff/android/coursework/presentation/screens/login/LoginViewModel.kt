package ru.tinkoff.android.coursework.presentation.screens.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.tinkoff.android.coursework.App
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.di.DaggerNetworkComponent
import ru.tinkoff.android.coursework.di.NetModule
import ru.tinkoff.android.coursework.domain.model.Result
import javax.inject.Inject

internal class LoginViewModel @Inject constructor(
    private val app: App
) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(serverUrl: String, username: String, password: String) {
        val netComponent = DaggerNetworkComponent.builder()
            .applicationComponent(app.applicationComponent)
            .netModule(NetModule(serverUrl))
            .build()
        app.networkComponent = netComponent

        val loginRepository = netComponent.getLoginRepository()
        loginRepository.zulipJsonApi = netComponent.getZulipJsonApi()
        viewModelScope.launch {
            loginRepository.login(username, password)
        }
        loginRepository.loginResult.observeForever { result ->
            if (result is Result.Success) {
                _loginResult.value =
                    LoginResult(success = LoggedInUserView(displayName = result.data.userName))
            } else {
                _loginResult.value = LoginResult(error = R.string.login_failed)
            }
        }

    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

}
