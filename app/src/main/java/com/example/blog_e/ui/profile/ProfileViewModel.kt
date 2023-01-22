package com.example.blog_e.ui.profile

import android.util.Log
import android.view.View
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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState (
    val errMsg: String = "",
)
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val blogRepo: BlogRepo,
) : ViewModel() {

    private var _profileUiState = MutableStateFlow(ProfileUiState())
    var profileUiState: StateFlow<ProfileUiState> = _profileUiState.asStateFlow()

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
    fun fetchPosts(pageSize: Int = 20, pageToken: String? = null) = viewModelScope.launch{
        val postsQueryModel = GetPostsQueryModel(
            usernames = listOf("me"),
            pageSize = pageSize,
            isUserFeed = false,
            pageToken = pageToken
        )
        val res = blogRepo.getPosts(postsQueryModel)
        when (res) {
            is ApiSuccess -> {
                // TODO: set next page token / store it
                res.data.nextPageToken
                res.data.posts
            }
            is ApiError -> {
                _profileUiState.update { it.copy(errMsg = res.message.toString())}
            }
            is ApiException -> {
                _profileUiState.update { it.copy(errMsg = res.e.message!!)}

            }
        }
    }

    companion object {
        const val DEFAULT_USER = "me"
    }
}