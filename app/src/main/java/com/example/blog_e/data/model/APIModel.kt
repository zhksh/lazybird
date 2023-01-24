package com.example.blog_e.data.model

import com.google.gson.annotations.SerializedName

data class PostAPIModel(
    val id: String,
    val content: String,
    val user: UserAPIModel,
    val timestamp: String,
    val likes: List<String>,
    val comments: List<CommentAPIModel>,
)

data class CommentAPIModel(
    val id: String,
    val user: UserAPIModel,
    val content: String,
)

data class UserAPIModel(
    @SerializedName("display_name")
    val displayName: String?,
    @SerializedName("icon_id")
    val iconId: String,
    @SerializedName("username")
    val username: String
)

data class GetUserAPIModel(
    @SerializedName("display_name")
    val displayName: String?,
    @SerializedName("icon_id")
    val iconId: String,
    @SerializedName("followers")
    val followers: List<UserAPIModel>,
    @SerializedName("username")
    val username: String,
    @SerializedName("bio")
    val selfDescription: String?
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
    val posts: ArrayList<PostAPIModel>
)

data class GetPostsQueryModel(
    val usernames: List<String>? = null,
    val pageSize: Int,
    val pageToken: String? = null,
    val isUserFeed: Boolean?= null
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

data class CompletePayload(
    val prefix: String,
    val temperature: Float,
    val mood: String,
    val ours: String
)

data class CommentPayload(
    val content: String
)


