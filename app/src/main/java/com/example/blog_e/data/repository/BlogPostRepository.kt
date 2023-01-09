package com.example.blog_e.data.repository

import androidx.lifecycle.LiveData
import com.example.blog_e.data.model.*


interface BlogPostRepository {

    fun getPostsStream(): LiveData<List<Post>>

    suspend fun getPosts(
        usernames: List<String>? = null,
        pageSize: Int,
        pageToken: String? = null,
        isUserFeed: Boolean
    ): List<Post>

    suspend fun refreshPosts()

    fun getPostStream(): ApiResult<LiveData<Post>>

    suspend fun getPost(post: Post): ApiResult<Post>

    suspend fun createPost(post: Post): ApiResult<PostAPIModel>

    suspend fun completePost(completePayload: CompletePayload): ApiResult<LLMResult>

    fun getCommentsStream(): ApiResult<LiveData<List<Comment>>>

    suspend fun getComments(post: Post): ApiResult<List<Comment>>

    suspend fun createComment(comment: Comment, post: Post): ApiResult<Any>

    suspend fun likeOrUnlikePost(like: Like, post: Post): ApiResult<Any>

}