package com.example.blog_e.ui.signUp

import android.view.View
import androidx.annotation.IdRes
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blog_e.R
import com.example.blog_e.data.model.NewUserAPIModel
import com.example.blog_e.data.model.ProfilePicture
import com.example.blog_e.data.repository.*
import com.example.blog_e.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SignUpState(
    val errorMessage: String? = null,
)

@HiltViewModel
class SignUpViewModel @Inject constructor() : ViewModel() {
    private val usernameRegex = """^[A-Za-z0-9]*$""".toRegex()
    private var username = ""
    private var displayName = ""
    private var password = ""

    val usernameError = ObservableField<String>()
    val passwordError = ObservableField<String>()
    val displayNameError = ObservableField<String>()

    val isLoading = ObservableBoolean(false)
    val signUpReady = ObservableBoolean(false)

    // Sending events as state https://developer.android.com/topic/architecture/ui-layer/events#handle-viewmodel-events
    private val _uiState = MutableStateFlow(SignUpState())
    val uiState = _uiState.asStateFlow()

    fun updatedUsername(s: CharSequence, start: Int, before: Int, count: Int) {
        this.username = s.toString()
        val error = this.validateUsername(this.username)
        this.usernameError.set(error)
        this.signUpReady.set(signUpIsReady())
    }

    fun updatedDisplayName(s: CharSequence, start: Int, before: Int, count: Int) {
        this.displayName = s.toString()
        this.signUpReady.set(signUpIsReady())
    }

    fun updatedPassword(s: CharSequence, start: Int, before: Int, count: Int) {
        this.password = s.toString()
        val error = this.validatePassword(this.password)
        this.passwordError.set(error)
        this.signUpReady.set(signUpIsReady())
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

        return ""
    }

    private fun signUpIsReady(): Boolean {
        return this.validateUsername(this.username) == "" &&
                this.validatePassword(this.password) == ""
    }
}