package com.example.blog_e.ui.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blog_e.Config
import com.example.blog_e.data.model.GetUserAPIModel
import com.example.blog_e.data.model.User
import com.example.blog_e.data.model.UserAPIModel
import com.example.blog_e.data.repository.ApiError
import com.example.blog_e.data.repository.ApiException
import com.example.blog_e.data.repository.ApiSuccess
import com.example.blog_e.data.repository.UserRepo
import com.example.blog_e.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val userRepo: UserRepo,
    private val sessionManager: SessionManager
) : ViewModel() {
    val tag = Config.tag(this.toString())
    private val users = MutableLiveData<List<UserAPIModel>>()

    fun searchResult(): LiveData<List<UserAPIModel>> {
        return users
    }

    fun findUsers(search: String): LiveData<List<UserAPIModel>> {
        viewModelScope.launch {
            when (val res = userRepo.findUsers(search)) {
                is ApiSuccess -> {
                    users.value = res.data.users
                }
                // TODO: Handle errors
                is ApiError -> Log.e(tag, "searchUser error")
                is ApiException -> Log.e(tag, "searchUser exception")
            }
        }

        return users
    }

    fun resetQuery() {
        users.value = emptyList()
    }
}