package com.example.blog_e.data.repository

import androidx.lifecycle.LiveData
import com.example.blog_e.data.model.Comment
import com.example.blog_e.data.model.Like
import com.example.blog_e.data.model.Post


interface BlogPostRepository {

    fun getPostsStream(): LiveData<List<Post>>

    suspend fun getPosts(): List<Post>

    suspend fun refreshPosts()

    fun getPostStream(): LiveData<Post>

    suspend fun getPost(post: Post): Post

    suspend fun createPost(post: Post)

    fun getCommentsStream(): LiveData<List<Comment>>

    suspend fun getComments(post: Post): List<Comment>

    suspend fun createComment(comment: Comment, post: Post)

    suspend fun likeOrUnlikePost(like: Like, post: Post)

}