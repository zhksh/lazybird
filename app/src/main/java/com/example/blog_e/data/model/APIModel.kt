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
    val username: String,
    @SerializedName("bio")
    val bio: String?
)

data class FindUsersAPIModel(
    @SerializedName("users")
    val users: List<UserAPIModel>
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
    val post: Post,
    val params: AutogenrationOptions
)

data class PostsResult(
    val nextPageToken: String,
    val posts: ArrayList<PostAPIModel>
)

data class GetPostsQueryModel(
    val usernames: List<String>? = null,
    val pageSize: Int,
    val pageToken: String? = null,
    val isUserFeed: Boolean? = null
)

data class NewUserAPIModel(
    val displayName: String,
    val password: String,
    val iconId: String,
    val username: String,
    var options: AutoCompleteOptions? = null
)

data class UpdateUserAPIModel(
    val displayName: String?,
    val iconId: String?,
    val password: String?,
    val bio: String?,
)

data class Authorization(
    val accessToken: String,
    val tokenType: String
)

data class LoginPayload(
    val username: String,
    val password: String
)

data class AutoCompleteOptions(
    val prefix: String = "",
    val temperature: Float,
    val mood: String,
    val ours: String = com.example.blog_e.Config.useGPTNeo
)

data class CommentPayload(
    val content: String
)

data class LLMResult(
    @SerializedName("response")
    val response: String,
    @SerializedName("prefix")
    val prefix: String
)

data class LLMSelfDescription(
    @SerializedName("response")
    val response: String,
    @SerializedName("entity")
    val entity: String
)



