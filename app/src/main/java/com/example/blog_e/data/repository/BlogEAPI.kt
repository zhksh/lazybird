package com.example.blog_e.data.repository

import com.example.blog_e.data.model.*
import retrofit2.Response
import retrofit2.http.*


//const val BASE_URL = "http://localhost:5432/"
const val BASE_URL = "https://mvsp-api.ncmg.eu"

interface BlogEAPI {

    // User
    @POST("users")
    suspend fun signUp(@Body params: NewUserAPIModel): Response<Authorization>

    @POST("users/auth")
    suspend fun login(@Body loginPayload: LoginPayload): Response<Authorization>

    @GET("users/{username}")
    suspend fun getUser(@Path("username") user: String): Response<GetUserAPIModel>

    @POST("users/{username}/follow")
    suspend fun follow(@Path("username") username: String): Response<FollowResult>

    // Post
    @POST("post")
    suspend fun createPost(@Body post: PostRequest): Response<PostAPIModel>

    @GET("posts")
    suspend fun getPosts(
        @Query("usernames") usernames: List<String>? = null,
        @Query("pageSize") pageSize: Int,
        @Query("pageToken") pageToken: String? = null,
        @Query("isUserFeed") isUserFeed: Boolean? = null,

        ): Response<PostsResult>



    @POST("generate/complete")
    suspend fun generateCompletion(@Body completePayload: CompletePayload): Response<LLMResult>


    /*TODO: APIs for
       (WS) Watch post  http://localhost:6969/posts/{id}/watch
       Like/Unlike post http://localhost:6969/posts/{id}/likes
       New comment      http://localhost:6969/posts/{id}/comments
     */

}