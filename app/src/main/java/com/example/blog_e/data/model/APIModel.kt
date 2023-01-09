package com.example.blog_e.data.model


data class PostAPIModel(
    val commentCount: Int,
    val content: String,
    val id: String,
    val likes: Int,
    val timestamp: String,
    val user: UserAPIModel
)

data class UserAPIModel(
    val displayName: String,
    val followers: Int,
    val iconId: String,
    val username: String
)

data class PostRequest(
    val content: String,
    val mood: String,
    val autogenerateAnswers: Boolean,
    val shouldAutoComplete: Boolean,
    val temperature: Int
)

data class PostsResult(
    val nextPageToken: String,
    val posts: List<PostAPIModel>
)

// TODO: change iconId to string
data class NewUserAPIModel(
    val displayName: String,
    val password: String,
    val iconId: String,
    val username: String
)

data class Authorization(
    val accessToken: String,
    val tokenType: String
)

data class LoginPayload(
    val username: String,
    val password: String
)

data class FollowResult(
    val code: Int,
    val message: String
)

data class CompletePayload(
    val prefix: String,
    val temperature: Float,
    val mood: String
)



