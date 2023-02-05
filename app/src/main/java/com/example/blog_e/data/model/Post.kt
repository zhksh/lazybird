package com.example.blog_e.data.model

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
    val autogenerateResponses: Boolean? = false
)

data class AutogenrationOptions(
    val mood: String,
    val temperature: Float,
    val historyLength: Int,
    val ours: String = com.example.blog_e.Config.useGPTNeo
)
