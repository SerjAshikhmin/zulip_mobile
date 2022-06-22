package ru.tinkoff.android.coursework.presentation.screens.login

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ru.tinkoff.android.coursework.App
import ru.tinkoff.android.coursework.MainActivity
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.databinding.ActivityLoginBinding
import ru.tinkoff.android.coursework.utils.startActivity

internal class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val serverUrl = binding.serverUrl
        val username = binding.username
        val password = binding.password
        val testAccountSwitch = binding.testAccountSwitch
        val login = binding.login
        val loading = binding.loading

        loginViewModel = ViewModelProvider(
            this,
            LoginViewModelFactory((application as App))
        )[LoginViewModel::class.java]

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            } else {
                username.error = null
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            } else {
                password.error = null
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                startActivity<MainActivity>()
                finish()
            }
        })

        username.doAfterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            doAfterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            serverUrl.text.toString(),
                            username.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }

            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                loginViewModel.login(
                    serverUrl.text.toString(),
                    username.text.toString(),
                    password.text.toString()
                )
            }
        }

        testAccountSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                serverUrl.setText(resources.getString(R.string.test_server_url))
                username.setText(resources.getString(R.string.test_account_login))
                password.setText(resources.getString(R.string.test_account_password))
            } else {
                serverUrl.setText("")
                username.setText("")
                password.setText("")
            }
        }
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }

}
