package com.example.blog_e.ui.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blog_e.Config
import com.example.blog_e.data.model.GetUserAPIModel
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

data class SearchUIState(
    val query: String = "",
    val isSearching: Boolean = false
)

data class ResultUIState(
    val isSuccessful: Boolean = false,
    val isUserNotFound: Boolean = false,
    val userAPIModel: GetUserAPIModel? = null,
    val isFollowing: Boolean = false
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val userRepo: UserRepo,
    private val sessionManager: SessionManager
) : ViewModel() {
    val TAG = Config.tag(this.toString())

    private val _text = MutableLiveData<String>().apply {
        value = "Recent searches"
    }
    val text: LiveData<String> = _text

    private val _searchUIState = MutableStateFlow(SearchUIState())
    val searchUIState: StateFlow<SearchUIState> = _searchUIState.asStateFlow()


    private val _resultUIState = MutableStateFlow(ResultUIState())
    val resultUIState: StateFlow<ResultUIState> = _resultUIState.asStateFlow()

    val currentUser = sessionManager.getUsername()

    suspend fun searchUser() {
        startSearching()
        val res = userRepo.getUser(_searchUIState.value.query)
        stopSearching()
        when (res) {
            is ApiSuccess -> {
                updateCurrentUserResult(res.data)
                Log.i(TAG, "searchUser: war erflogreich")
            }
            is ApiError -> {
                revokeResultState()
                Log.i(TAG, "searchUser: war fehler mit Message ${res.message}")
            }
            is ApiException -> Log.i(TAG, "searchUser: hat einen Fehler geworfen")
        }
    }

    private fun startSearching() {
        _searchUIState.update {
            it.copy(
                isSearching = true
            )
        }
    }

    private fun revokeResultState() {
        _resultUIState.update {
            it.copy(
                userAPIModel = null,
                isSuccessful = false,
                isUserNotFound = true
            )
        }
    }

    fun clearLastResult() {
        _resultUIState.update {
            it.copy(
                userAPIModel = null,
                isSuccessful = false,
                isFollowing = false,
                isUserNotFound = false
            )
        }
    }

    private fun updateCurrentUserResult(data: GetUserAPIModel) {
        val isFollowing = data.followers.map { it.username }.contains(currentUser)
        _resultUIState.update {
            it.copy(
                userAPIModel = data,
                isSuccessful = true,
                isFollowing = isFollowing,
                isUserNotFound = false
            )
        }
    }

    fun updateQuery(query: String) {
        _searchUIState.update {
            it.copy(query = query)
        }
    }

    private fun stopSearching() {
        _searchUIState.update {
            it.copy(isSearching = false)
        }
    }

    fun followUser() {
        viewModelScope.launch {
            userRepo.followOrUnfollowUser(
                resultUIState.value.userAPIModel!!.username,
                false
            )
            updateFollowStatus(true)
        }
    }

    fun unFollowUser() {
        viewModelScope.launch {
            userRepo.followOrUnfollowUser(
                resultUIState.value.userAPIModel!!.username,
                true
            )
            updateFollowStatus(false)
        }
    }

    private fun updateFollowStatus(isFollowingNow: Boolean) {
        _resultUIState.update {
            it.copy(isFollowing = isFollowingNow)
        }
    }
}