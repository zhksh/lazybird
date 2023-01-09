package com.example.blog_e.data.model

import java.util.*


data class Post(
    val id: UUID = UUID.randomUUID(),
    val content: String,
    val publicationDate: Date,
    val commentCount: Int,
    val likes: List<Like>? = emptyList(),
    val author: User? = null,
    val autogenerateResponses: Boolean? = false,
    val isAIEnabled: Boolean = false,
)