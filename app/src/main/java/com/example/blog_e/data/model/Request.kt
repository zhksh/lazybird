package com.example.blog_e.data.model


data class PostRequest(
    val content: String,
    val mood: String,
    val autogenerateAnswers: Boolean,
    val shouldAutoComplete: Boolean,
    val temperature: Int
)