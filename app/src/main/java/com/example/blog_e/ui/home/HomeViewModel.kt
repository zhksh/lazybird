package com.example.blog_e.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.blog_e.data.model.PostAPIModel
import com.example.blog_e.data.repository.*
import com.example.blog_e.models.PostsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepo: UserRepo,
    private val blogRepo: BlogRepo
) : ViewModel() {

    private val _posts: MutableLiveData<List<PostsViewModel>> = MutableLiveData()

    fun getPosts(): LiveData<List<PostsViewModel>> {
        return _posts
    }

    suspend fun fetchBlogs(
        isUserFeed: Boolean,
        pageSize: Int = 20,
        pageToken: String? = null
    ): List<PostAPIModel> {
        //TODO: fetch global instead of me, add flag for followers
        val postsResult = blogRepo.getPosts(
            pageSize = pageSize,
            isUserFeed = isUserFeed,
            pageToken = pageToken
        )

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
}

