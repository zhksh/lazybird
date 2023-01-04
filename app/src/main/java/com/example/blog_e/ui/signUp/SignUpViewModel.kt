package com.example.blog_e.ui.signUp

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blog_e.data.model.ProfilePicture
import com.example.blog_e.data.model.User
import com.example.blog_e.data.repository.UserRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignUpViewModel(private val userRepo: UserRepo) : ViewModel() {

    val username = MutableLiveData<String>().apply { value="" }
    val password = MutableLiveData<String>()
    val profilePicture = MutableLiveData<ProfilePicture>()

    private var userMutableLiveData: MutableLiveData<User>? = null

    fun getUser(): MutableLiveData<User>? {
        if (userMutableLiveData == null) {
            userMutableLiveData = MutableLiveData<User>()
        }
        return userMutableLiveData
    }

    fun signUp() {
        val newUser = User(
            username = username.value!!,
            password = password.value!!,
            profilePicture = profilePicture.value!!
        )

        Log.d("Hier", newUser.toString())
        println(newUser)
        userMutableLiveData!!.value = newUser

        viewModelScope.launch(Dispatchers.IO) {
            userRepo.signUp(userMutableLiveData!!.value!!)
        }

    }
}