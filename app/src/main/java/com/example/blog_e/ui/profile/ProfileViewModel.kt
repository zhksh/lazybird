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
import com.example.blog_e.data.model.GetPostsQueryModel
import com.example.blog_e.data.model.PostAPIModel
import com.example.blog_e.data.model.ProfilePicture
import com.example.blog_e.data.model.User
import com.example.blog_e.data.repository.*
import com.example.blog_e.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val blogRepo: BlogRepo,
    private val userRepo: UserRepo
) : ViewModel() {

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
        }.liveData
            .cachedIn(viewModelScope) as MutableLiveData<PagingData<PostAPIModel>>


    var posts: LiveData<PagingData<PostAPIModel>> = _posts

    fun getProfile(): LiveData<User?> {
        loadUserData()
        return user
    }

    fun onClickLogout(view: View) {
        sessionManager.deleteAuthToken()
        loadUserData()
    }

    private fun loadUserData() {
        val token = sessionManager.fetchAuthToken()
        if (token != null) {
            // TODO: Actually implement me
            user.value = User(
                username = "TestUser",
                password = "not actually",
                profilePicture = ProfilePicture.PICTURE_00
            )
        } else {
            user.value = null
        }
    }

    suspend fun fetchBlogs(
        pageSize: Int = 20,
        pageToken: String? = null
    ): List<PostAPIModel> {
        val postsQueryModel = GetPostsQueryModel(
            usernames = listOf("me"),
            pageSize = pageSize,
            isUserFeed = false,
            pageToken = pageToken
        )

        val postsResult = blogRepo.getPosts(postsQueryModel)


        return when (postsResult) {
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