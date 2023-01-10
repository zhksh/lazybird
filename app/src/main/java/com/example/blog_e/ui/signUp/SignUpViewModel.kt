package com.example.blog_e.ui.signUp

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.example.blog_e.data.model.User
import com.example.blog_e.data.repository.ApiError
import com.example.blog_e.data.repository.ApiException
import com.example.blog_e.data.repository.ApiSuccess
import com.example.blog_e.data.repository.UserRepo
import com.example.blog_e.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Observable
import javax.inject.Inject


@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val userRepo: UserRepo,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val usernameRegex = """^[A-Za-z0-9]*$""".toRegex()

    var usernameError = ObservableField<String>()
    var passwordError = ObservableField<String>()

    suspend fun signUp(user: User): Boolean =
        when (val authorizationResult = userRepo.signUp(user)) {
            is ApiSuccess -> {
                sessionManager.saveAuthToken(authorizationResult.data.accessToken)
                true
            }
            is ApiError -> {
                false
            }
            is ApiException -> throw authorizationResult.e
        }

    fun validateUsername(name: String) {
        if (name.length < 3) {
            this.usernameError.set("username too short")
            return
        }

        if (name.length > 15) {
            this.usernameError.set("username too long")
            return
        }

        if (!this.usernameRegex.matches(name)) {
            this.usernameError.set("only letters and numbers allowed in username")
            return
        }

        this.usernameError.set("")
    }

    fun validatePassword(password: String) {
        if (password.length < 6) {
            this.passwordError.set("password too short")
            return
        }

        this.passwordError.set("")
    }
}