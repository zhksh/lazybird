package com.example.blog_e.ui.signUp

import androidx.lifecycle.ViewModel
import com.example.blog_e.data.model.User
import com.example.blog_e.data.repository.ApiError
import com.example.blog_e.data.repository.ApiException
import com.example.blog_e.data.repository.ApiSuccess
import com.example.blog_e.data.repository.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class SignUpViewModel @Inject constructor(val userRepo: UserRepo) : ViewModel() {

    suspend fun signUp(user: User): Boolean =
        when (val authorizationResult = userRepo.signUp(user)) {
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