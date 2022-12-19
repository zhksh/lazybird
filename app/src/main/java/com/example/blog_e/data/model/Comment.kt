package com.example.blog_e.data.model

import java.util.*


data class Comment(
    val id: UUID = UUID.randomUUID(),
    val content: String,
    val correspondingPost: Post,
    val numberComment: Int,
    val author: User
)