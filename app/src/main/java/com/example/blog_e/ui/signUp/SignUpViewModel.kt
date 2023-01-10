package com.example.blog_e.ui.signUp

import android.view.View
import android.widget.Toast
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.blog_e.R
import com.example.blog_e.data.model.ProfilePicture
import com.example.blog_e.data.model.User
import com.example.blog_e.data.repository.ApiError
import com.example.blog_e.data.repository.ApiException
import com.example.blog_e.data.repository.ApiSuccess
import com.example.blog_e.data.repository.UserRepo
import com.example.blog_e.utils.SessionManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Observable
import javax.inject.Inject


@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val userRepo: UserRepo,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val usernameRegex = """^[A-Za-z0-9]*$""".toRegex()
    private var username = ""
    private var password = ""

    val usernameError = ObservableField<String>()
    val passwordError = ObservableField<String>()
    val isLoading = ObservableBoolean(false)

    fun onClickSignUp(view: View) {
        val newUser = User(
            username = this.username,
            password = this.password,
            profilePicture = ProfilePicture.PICTURE_01  // TODO: Replace with selected image
        )

        viewModelScope.launch {
            isLoading.set(true)

            val success = signUp(newUser)
            if (success) {
                println("logged in success")
            } else {
                println("logged in failure")
            }

            isLoading.set(false)
        }
    }

    fun updatedUsername(s: CharSequence, start: Int, before: Int, count: Int) {
        this.username = s.toString()
        val error = this.validateUsername(this.username)
        this.usernameError.set(error)
    }

    fun updatedPassword(s: CharSequence, start: Int, before: Int, count: Int) {
        this.password = s.toString()
        val error = this.validatePassword(this.password)
        this.passwordError.set(error)
    }

    private fun validateUsername(name: String): String {
        if (name.length < 3) {
            return "username too short"
        }

        if (name.length > 15) {
            return "username too long"
        }

        if (!this.usernameRegex.matches(name)) {
            return "only letters and numbers allowed in username"
        }

        return ""
    }

    private fun validatePassword(password: String): String {
        if (password.length < 6) {
            return "password too short"
        }

        return  ""
    }

    suspend fun signUp(user: User): Boolean =
        when (val authorizationResult = userRepo.signUp(user)) {
            is ApiSuccess -> {
                sessionManager.saveAuthToken(authorizationResult.data.accessToken)
                true
            }
            is ApiError -> {
                false       // TODO: Display api error
            }
            is ApiException -> throw authorizationResult.e
        }
}