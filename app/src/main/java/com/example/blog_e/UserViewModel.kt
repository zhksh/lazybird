package com.example.blog_e

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blog_e.data.model.LoginPayload
import com.example.blog_e.data.model.NewUserAPIModel
import com.example.blog_e.data.model.User
import com.example.blog_e.data.model.mapApiUser
import com.example.blog_e.data.repository.*
import com.example.blog_e.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepo: UserRepo,
    private val sessionManager: SessionManager,
) : ViewModel() {
    private val user: MutableLiveData<User?> = MutableLiveData<User?>()

    init {
        if (sessionManager.fetchAuthToken() == null) {
            user.value = null
        } else {
            viewModelScope.launch {
                fetchUser()
            }
        }
    }

    fun getUser(): LiveData<User?> {
        // TODO: Check if observers are also notified, if value was already set before.
        return user
    }

    fun login(username: String, password: String): LiveData<User?> {
        viewModelScope.launch {
            when (val result = userRepo.login(LoginPayload(username, password))) {
                is ApiSuccess -> {
                    sessionManager.saveSession(result.data.accessToken, username)
                    fetchUser()
                }
                is ApiError -> {
                    // TODO: Show error to user
                }
                is ApiException -> throw result.e
            }
        }

        return user
    }

    fun signUp(newUser: NewUserAPIModel): LiveData<User?> {
        viewModelScope.launch {
            when (val result = userRepo.signUp(newUser)) {
                is ApiSuccess -> {
                    sessionManager.saveSession(result.data.accessToken, newUser.username)
                    fetchUser()
                }
                is ApiError -> {
                    // TODO: Show error to user
                }
                is ApiException -> throw result.e
            }
        }

        return user
    }

    fun logout() {
        sessionManager.resetSession()
        user.value = null
    }

    private suspend fun fetchUser() {
        when (val result = userRepo.getUser("me")) {
            is ApiSuccess -> {
                user.value = mapApiUser(result.data)
            }
            is ApiError -> {
                sessionManager.resetSession()
                user.value = null
            }
            is ApiException -> {
                sessionManager.resetSession()
                user.value = null
            }
        }
    }
}