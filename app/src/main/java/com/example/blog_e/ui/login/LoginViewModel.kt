package com.example.blog_e.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blog_e.data.model.LoginPayload
import com.example.blog_e.data.repository.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(val userRepo: UserRepo) : ViewModel() {

    fun login(loginPayload: LoginPayload) {
        viewModelScope.launch(Dispatchers.IO) {
            val authorization = userRepo.login(loginPayload)
            //TODO: save auth token

            //TODO: handle unsuccessfull events like password wrong
        }
    }
}