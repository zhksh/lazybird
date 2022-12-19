package com.example.blog_e.data.network

import com.example.blog_e.data.model.Comment
import com.example.blog_e.data.model.Emotion
import com.example.blog_e.data.model.Post


// If an option value is null, then it isn't provided as options parameter
abstract class AITextGeneratorOptions(
    text: String? = "",
    emotion: Emotion?,
    temperature: Float = 0f
)

data class PostGeneratorOptions(
    val prompt: String?,
    val emotion: Emotion?,
    val temperature: Float,
    val autoGenerateResponses: Boolean,
    val postList: List<Post>?
) : AITextGeneratorOptions(prompt, emotion, temperature)

data class CommentGeneratorOptions(
    val prompt: String?,
    val emotion: Emotion?,
    val temperature: Float,
    val post: Post,
    val priorCommentsList: List<Comment>?
) : AITextGeneratorOptions(prompt, emotion, temperature)