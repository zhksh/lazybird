package com.example.blog_e.ui.login

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {
    val isLoading = ObservableBoolean(false)
    val loginReady = ObservableBoolean(false)

    private var username = ""
    private var password = ""

    fun updatedUsername(s: CharSequence, start: Int, before: Int, count: Int) {
        this.username = s.toString()
        loginReady.set(inputIsValid())
    }

    fun updatedPassword(s: CharSequence, start: Int, before: Int, count: Int) {
        this.password = s.toString()
        loginReady.set(inputIsValid())
    }

    private fun inputIsValid(): Boolean {
        return username != "" && password != ""
    }
}


