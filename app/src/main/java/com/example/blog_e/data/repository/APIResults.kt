package com.example.blog_e.data.repository

import com.google.gson.annotations.SerializedName


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
