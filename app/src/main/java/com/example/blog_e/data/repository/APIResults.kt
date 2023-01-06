package com.example.blog_e.data.repository

import com.google.gson.annotations.SerializedName

class UserResult(
    @SerializedName("username")
    val username: String,

    @SerializedName("followers")
    val followerCount: Int,

    @SerializedName("iconId")
    val iconId: String,

    @SerializedName("displayName")
    val displayName: String,
    )
data class LLMResult(
    @SerializedName("response")
    val response: String,
)


