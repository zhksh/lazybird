package com.example.blog_e.ui.signUp

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.example.blog_e.utils.validatePassword
import com.example.blog_e.utils.validateUsername
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor() : ViewModel() {
    private var username = ""
    private var password = ""

    val usernameError = ObservableField<String>()
    val passwordError = ObservableField<String>()
    val displayNameError = ObservableField<String>()
    val displayName = ObservableField("")

    val isLoading = ObservableBoolean(false)
    val signUpReady = ObservableBoolean(false)

    fun updatedUsername(s: CharSequence, start: Int, before: Int, count: Int) {
        if (this.username == displayName.get()) {
            displayName.set(s.toString())
        }
        this.username = s.toString()
        val error = validateUsername(this.username)
        this.usernameError.set(error)
        this.signUpReady.set(signUpIsReady())
    }

    fun updatedDisplayName(s: CharSequence, start: Int, before: Int, count: Int) {
        this.displayName.set(s.toString())
        if (s.toString().isEmpty()) {
            displayNameError.set("Please enter a display name")
        }

        this.signUpReady.set(signUpIsReady())
    }

    fun updatedPassword(s: CharSequence, start: Int, before: Int, count: Int) {
        this.password = s.toString()
        val error = validatePassword(this.password)
        this.passwordError.set(error)
        this.signUpReady.set(signUpIsReady())
    }

    private fun signUpIsReady(): Boolean {
        val displayName = displayName.get() ?: return false
        return validateUsername(this.username) == "" && validatePassword(this.password) == ""&& displayName.isNotEmpty()
    }
}