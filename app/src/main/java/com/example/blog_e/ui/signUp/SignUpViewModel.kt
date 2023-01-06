package com.example.blog_e.ui.signUp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blog_e.data.model.User
import com.example.blog_e.data.repository.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SignUpViewModel @Inject constructor(val userRepo: UserRepo) : ViewModel() {

    fun signUp(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            val authorization = userRepo.signUp(user)
            //TODO: save auth token

            //TODO: handle unsuccessfull events e.g. this user is already registered
        }
    }

}