package com.example.blog_e.models

data class PostsViewModel(
    val profilePicture: Int,
    val username: String,
    val nickname: String,
    val createdSince: String,
    val content: String,
    val heartIcon: Int,
    val likeNumber: Number,
    val commentIcon: Int,
    val commentNumber: Number
)