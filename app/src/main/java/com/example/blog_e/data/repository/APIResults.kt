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

/*
sealed class ResultWrapper<out T> {
    data class Success<out T>(val value: T): ResultWrapper<T>()
    data class GenericError(val code: Int? = null, val error: ErrorResponse? = null): ResultWrapper<Nothing>()
    object NetworkError: ResultWrapper<Nothing>()
}*/
