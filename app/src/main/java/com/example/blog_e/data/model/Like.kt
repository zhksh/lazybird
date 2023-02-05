package com.example.blog_e.data.model

import java.util.*

data class Like(
    val id: UUID,
    val correspondingPost: Post,
    val user: User
)
