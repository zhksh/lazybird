package com.example.blog_e.ui.signUp

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

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