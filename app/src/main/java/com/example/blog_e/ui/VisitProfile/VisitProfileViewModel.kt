package com.example.blog_e.ui.VisitProfile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.liveData
import com.example.blog_e.data.model.GetUserAPIModel
import com.example.blog_e.data.model.PostAPIModel
import com.example.blog_e.data.repository.*
import com.example.blog_e.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class VisitUserUIModel(
    val user: GetUserAPIModel? = GetUserAPIModel(
        username = "",
        iconId = "0",
        displayName = "",
        followers = emptyList()
    ),
    val isFollowing: Boolean = false
)


@HiltViewModel
class ProfileVisitViewModel @Inject constructor(
    private val blogRepo: BlogRepo,
    private val userRepo: UserRepo,
    private val sessionManager: SessionManager
) : ViewModel() {

    private var _uiState = MutableStateFlow(VisitUserUIModel())
    val uiState = _uiState.asStateFlow()

    private val currentUser = sessionManager.getUsername()

    private var _posts: MutableLiveData<PagingData<PostAPIModel>> = MutableLiveData()

    val posts: LiveData<PagingData<PostAPIModel>> = _posts


    fun initPager(username: String) {
        val pager = Pager(
            blogRepo.getDefaultPageConfig(),
        ) {
            PostPagingSource(
                blogRepo,
                isUserFeed = false,
                usernames = listOf(username)
            )
        }

        _posts =
            pager.liveData.cachedIn(viewModelScope) as MutableLiveData<PagingData<PostAPIModel>>
    }

    suspend fun fetchUser(currentUser: String) {
        when (val result = userRepo.getUser(currentUser)) {
            is ApiSuccess -> {
                updateUiWithUser(result.data)
            }
            is ApiError -> {
                TODO()
            }
            is ApiException -> {
                TODO()
            }
        }
    }

    private fun updateUiWithUser(userAPIModel: GetUserAPIModel) {
        val isFollowing = userAPIModel.followers.map { it.username }.contains(currentUser)
        _uiState.update {
            it.copy(
                user = userAPIModel,
                isFollowing = isFollowing
            )
        }
    }
}