package com.example.blog_e.ui.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blog_e.Config
import com.example.blog_e.data.model.ProfilePicture
import com.example.blog_e.data.model.iconIdToProfilePicture
import com.example.blog_e.data.repository.ApiError
import com.example.blog_e.data.repository.ApiException
import com.example.blog_e.data.repository.ApiSuccess
import com.example.blog_e.data.repository.UserRepo
import com.example.blog_e.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchResult(
    val username: String,
    val displayName: String,
    val bio: String,
    val icon: ProfilePicture,
    val isOwnUser: Boolean
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    sessionManager: SessionManager,
    private val userRepo: UserRepo,
) : ViewModel() {
    val tag = Config.tag(this.toString())
    private val users = MutableLiveData<List<SearchResult>>()
    private val ownUsername = sessionManager.getUsername()

    fun searchResult(): LiveData<List<SearchResult>> {
        return users
    }

    fun findUsers(search: String): LiveData<List<SearchResult>> {
        viewModelScope.launch {
            when (val res = userRepo.findUsers(search)) {
                is ApiSuccess -> {
                    val result = res.data.users.map { user ->
                        SearchResult(
                            username = user.username,
                            displayName = user.displayName ?: user.username,
                            bio = user.bio ?: "",
                            icon = iconIdToProfilePicture(user.iconId),
                            isOwnUser = user.username == ownUsername
                        )
                    }
                    users.value = result
                }
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
