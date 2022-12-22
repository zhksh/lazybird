package com.example.blog_e.data.model


data class PostAPIModel(
    val commentCount: Int,
    val content: String,
    val id: String,
    val likes: Int,
    val timestamp: String,
    val user: UserAPIModel
)

data class UserAPIModel(
    val displayName: String,
    val followers: Int,
    val iconId: String,
    val username: String
)