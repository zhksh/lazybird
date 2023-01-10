package com.example.blog_e.ui.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.blog_e.data.model.PostAPIModel
import com.example.blog_e.data.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val blogRepo: BlogRepo,
    private val userRepo: UserRepo
) : ViewModel() {

    suspend fun fetchBlogs(
        pageSize: Int = 20,
        pageToken: String? = null
    ): List<PostAPIModel> {
        val postsResult = blogRepo.getPosts(
            usernames = listOf("me"),
            pageSize = pageSize,
            isUserFeed = false,
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