package com.example.blog_e.data.model

import com.example.blog_e.Config
import com.example.blog_e.utils.Utils
import java.time.LocalDateTime
import java.util.*


data class Post(
    val id: UUID = UUID.randomUUID(),
    val content: String,
    val publicationDate: LocalDateTime = Utils.currentDate(),
    val commentCount: Int = 0,
    val likes: List<Like>? = emptyList(),
    val author: User? = null,
    val autogenerateResponses: Boolean? = false,
    val isAIEnabled: Boolean = false,
)