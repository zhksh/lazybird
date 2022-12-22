package com.example.blog_e.data.repository

import com.example.blog_e.data.model.*
import retrofit2.Response
import retrofit2.http.*


const val BASE_URL = "http://localhost:5432/"

interface BackendService {

    @GET("users/{username}")
    suspend fun getUser(@Path("username") user: String, @Header("Authorization")  authorization: String): Response<User>

    @POST("users")
    suspend fun signUp(@Body params: CreateUser): Response<SignUpResponse>

    @POST("post")
    suspend fun createPost(@Body post: PostRequest): Response<PostAPIModel>

}