package com.example.blog_e.models

// TODO: Can this be removed?
data class PostsViewModel(
    val profilePicture: Int,
    val username: String,
    val createdSince: String = "10h",
    val content: String = "Hello World Hello World Hello World ",
    val heartIcon: Int = 2,
    val likeNumber: Number = 2,
    val commentIcon: Int = 2,
    val commentNumber: Number = 2
)