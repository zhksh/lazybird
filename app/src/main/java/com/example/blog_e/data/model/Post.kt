package com.example.blog_e.data.model

import java.util.*


data class Post(
    val id: UUID = UUID.randomUUID(),
    val content: String,
    val publicationDate: Date,
    val modifications: String,
    val comments: List<Comment>,
    val likes: List<Like>,
    val author: User,
    val isAIEnabled: Boolean = false,
)