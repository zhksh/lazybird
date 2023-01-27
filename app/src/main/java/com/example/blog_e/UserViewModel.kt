package com.example.blog_e

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blog_e.data.model.*
import com.example.blog_e.data.repository.*
import com.example.blog_e.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


data class SelfDescription(
    val bio: String = "",
    val err: SuccessResponse? = null
)

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepo: UserRepo,
    private val sessionManager: SessionManager,
) : ViewModel() {
    private val user: MutableLiveData<User?> = MutableLiveData<User?>()
    private val TAG = Config.tag(this.toString())

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
        return user
    }

    fun login(username: String, password: String): LiveData<SuccessResponse> {
        val response = MutableLiveData<SuccessResponse>()

        viewModelScope.launch {
            when (val result = userRepo.login(LoginPayload(username, password))) {
                is ApiSuccess -> {
                    sessionManager.saveSession(result.data.accessToken, username)
                    fetchUser()
                    response.value = SuccessResponse(errorMessage = null)
                }
                is ApiError -> {
                    when (result.code) {
                        401 -> response.value = SuccessResponse(errorMessage = "Password is incorrect")
                        404 -> response.value = SuccessResponse(errorMessage = "Username is incorrect")
                        else -> response.value = SuccessResponse(errorMessage = "Something went wrong, please try again")
                    }
                }
                is ApiException -> response.value = SuccessResponse(errorMessage = "Connection failed")
            }
        }

        return response
    }

    fun updateUserInfo(info: UpdateUserAPIModel): LiveData<SuccessResponse> {
        val response = MutableLiveData<SuccessResponse>()

        viewModelScope.launch {
            when (userRepo.updateUser("me", info)) {
                is ApiSuccess -> response.value = SuccessResponse(null)
                is ApiError -> response.value = SuccessResponse("Something went wrong, please try again")
                is ApiException -> response.value = SuccessResponse("Connection failed")
            }

            fetchUser()
        }

        return response
    }

    fun createSelfDesc(): LiveData<SelfDescription> {
        val response = MutableLiveData<SelfDescription>()

        viewModelScope.launch {
            val res = userRepo.createSelfDescription(AutoCompleteOptions("",
                Config.defaultTemperature, Config.defaultMood, "false"))
            when(res){
                is ApiSuccess ->  {
                    response.value = SelfDescription(bio = res.data.response)
                }
                is ApiError ->{
                    Log.e(TAG, "generating Desc for ${user.value?.username} failed ")
                    response.value = SelfDescription(err = SuccessResponse("Connection failed"))
                }
                is ApiException -> {
                    response.value = SelfDescription(err = SuccessResponse("Connection failed"))
                }
            }
        }
        return response
    }

    fun signUp(newUser: NewUserAPIModel): LiveData<SuccessResponse> {
        val response = MutableLiveData<SuccessResponse>()

        viewModelScope.launch {
            when (val result = userRepo.signUp(newUser)) {
                is ApiSuccess -> {
                    sessionManager.saveSession(result.data.accessToken, newUser.username)
                    fetchUser()
                    response.value = SuccessResponse(errorMessage = null)
                }
                is ApiError -> {
                    when (result.code) {
                        409 -> response.value = SuccessResponse(errorMessage = "This username is already taken")
                        else -> response.value = SuccessResponse(errorMessage = "Something went wrong, please try again")
                    }
                }
                is ApiException -> response.value = SuccessResponse(errorMessage = "Connection failed")
            }
        }

        return response
    }

    fun logout() {
        sessionManager.resetSession()
        user.value = null
    }

    fun renewUserData(){
        viewModelScope.launch {
            fetchUser()
        }
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