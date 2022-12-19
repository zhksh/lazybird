package com.example.blog_e.data.model

import java.util.*


data class User(
    val id: UUID,
    val username: String,
    // TODO anders mit umgehen
    val password: String,
    // Resource
    val profilePicutre: Int
)
