package com.example.blog_e.data.network

interface LLMOperations {

    fun generatePostText(postOptions: PostGeneratorOptions): String

    fun generateCommentText(commentOptions: CommentGeneratorOptions): String
}