package com.example.blog_e.ui.signUp

import androidx.lifecycle.ViewModel
import com.example.blog_e.data.model.User
import com.example.blog_e.data.repository.ApiError
import com.example.blog_e.data.repository.ApiException
import com.example.blog_e.data.repository.ApiSuccess
import com.example.blog_e.data.repository.UserRepo
import com.example.blog_e.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val userRepo: UserRepo,
    private val sessionManager: SessionManager
) : ViewModel() {

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

}