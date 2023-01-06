package com.example.blog_e.ui.login

import androidx.lifecycle.ViewModel
import com.example.blog_e.data.model.LoginPayload
import com.example.blog_e.data.repository.ApiError
import com.example.blog_e.data.repository.ApiException
import com.example.blog_e.data.repository.ApiSuccess
import com.example.blog_e.data.repository.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(val userRepo: UserRepo) : ViewModel() {

    suspend fun login(loginPayload: LoginPayload): Boolean =
        when (val authorizationResult = userRepo.login(loginPayload)) {
            is ApiSuccess -> {
                //TODO set auth token
                true
            }
            is ApiError -> {
                false
            }
            is ApiException -> throw authorizationResult.e
        }
}