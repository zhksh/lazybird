package com.example.blog_e.ui.profile

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.liveData
import com.example.blog_e.Config
import com.example.blog_e.data.model.*
import com.example.blog_e.data.repository.*
import com.example.blog_e.utils.SessionManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState (
    val user: GetUserAPIModel? = null,
    val errMsg: String = "",
    val logout: Boolean = false
)
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val blogRepo: BlogRepo,
    private val userRepo: UserRepo
) : ViewModel() {

    private val TAG = Config.tag(this.toString())

    private var _profileUiState = MutableStateFlow(ProfileUiState())
    var profileUiState: StateFlow<ProfileUiState> = _profileUiState.asStateFlow()

    private val user: MutableLiveData<User?> = MutableLiveData()

    private var _posts: MutableLiveData<PagingData<PostAPIModel>> =
        Pager(
            blogRepo.getDefaultPageConfig(),
        ) {
            PostPagingSource(
                blogRepo,
                isUserFeed = false,
                usernames = listOf(DEFAULT_USER)
            )
        }.liveData.cachedIn(viewModelScope) as MutableLiveData<PagingData<PostAPIModel>>


    var posts: LiveData<PagingData<PostAPIModel>> = _posts
    fun onClickLogout(view: View) {
        sessionManager.resetSession()
        _profileUiState.update { it.copy(logout = true) }
    }

    fun loadUserData() = viewModelScope.launch{
        val token = sessionManager.fetchAuthToken()
        val username = sessionManager.getUsername()
        if (token != null && username != null) {

            val res = userRepo.getUser(username)
            when (res) {
                is ApiException -> {
                    _profileUiState.update { it.copy(errMsg = res.e.message!!)}
                }
                is ApiError -> {
                    _profileUiState.update { it.copy(logout = true) }
                }
                is ApiSuccess -> {
                    _profileUiState.update { it.copy(user = res.data, errMsg = "") }
                }
            }
        }
    }

    fun fetchPosts(pageSize: Int = 20, pageToken: String? = null) = viewModelScope.launch{
        val postsQueryModel = GetPostsQueryModel(
            usernames = listOf("me"),
            pageSize = pageSize,
            isUserFeed = false,
            pageToken = pageToken
        )
        val postsResult = blogRepo.getPosts(postsQueryModel)
        when (postsResult) {
            is ApiSuccess -> {
                // TODO: set next page token / store it
                postsResult.data.nextPageToken
                postsResult.data.posts
            }
            is ApiError -> {
                Log.e(this.toString(), "Wähhhhhh") // do something
                emptyList()
            }
            is ApiException -> throw postsResult.e
        }
    }

    companion object {
        const val DEFAULT_USER = "me"
    }

}