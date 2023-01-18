package com.example.blog_e.ui.login

import android.view.View
import androidx.annotation.IdRes
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blog_e.R
import com.example.blog_e.data.model.LoginPayload
import com.example.blog_e.data.repository.*
import com.example.blog_e.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginState(
    val errorMessage: String? = null,
    @IdRes val navTo: Int? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepo: UserRepo,
    private val sessionManager: SessionManager
) : ViewModel() {

    val isLoading = ObservableBoolean(false)
    val loginReady = ObservableBoolean(false)

    private var username = ""
    private var password = ""

    private val _uiState = MutableStateFlow(LoginState())
    val uiState = _uiState.asStateFlow()

    fun onClickLogIn(view: View) {
        val payload = makeLoginPayload() ?: return

        viewModelScope.launch {
            isLoading.set(true)

            // TODO: Do we need to catch error?
            val success = login(payload)
            if (success) {
                _uiState.update {
                    val navId = R.id.action_login_fragment_to_navigation_home
                    it.copy(navTo = navId)
                }
            } else {
                _uiState.update {
                    it.copy(errorMessage = "failed to log in")
                }
            }

            isLoading.set(false)
        }
    }

    fun onClickNoAccount(view: View) {
        _uiState.update {
            val navId = R.id.action_login_fragment_to_sign_up_fragment
            it.copy(navTo = navId)
        }
    }

    fun updatedUsername(s: CharSequence, start: Int, before: Int, count: Int) {
        this.username = s.toString()
        loginReady.set(inputIsValid())
    }

    fun updatedPassword(s: CharSequence, start: Int, before: Int, count: Int) {
        this.password = s.toString()
        loginReady.set(inputIsValid())
    }

    suspend fun login(loginPayload: LoginPayload): Boolean =
        when (val authorizationResult = userRepo.login(loginPayload)) {
            is ApiSuccess -> {
                sessionManager.saveSession(authorizationResult.data.accessToken, loginPayload.username)
                true
            }
            is ApiError -> {
                false
            }
            is ApiException -> throw authorizationResult.e
        }

    private fun makeLoginPayload(): LoginPayload? {
        if (inputIsValid()) {
            return LoginPayload(username = username, password = password)
        }

        return null
    }

    private fun inputIsValid(): Boolean {
        return username != "" && password != ""
    }
}


