package com.example.blog_e.data.api

import com.example.blog_e.SelfDescription
import com.example.blog_e.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface BlogEAPI {


    // User routes
    @POST("users")
    suspend fun signUp(@Body user: NewUserAPIModel): Response<Authorization>

    @GET("users")
    suspend fun findUsers(@Query("search") search: String): Response<FindUsersAPIModel>

    @POST("users/auth")
    suspend fun login(@Body loginPayload: LoginPayload): Response<Authorization>

    @GET("users/{username}")
    suspend fun getUser(@Path("username") user: String): Response<GetUserAPIModel>

    @POST("users/{username}/follow")
    @Headers("Content-Type: text/plain")
    suspend fun follow(@Path("username") username: String): Response<String>

    @DELETE("users/{username}/follow")
    @Headers("Content-Type: text/html")
    suspend fun unFollow(@Path("username") username: String): Response<String>

    @POST("users/{username}")
    suspend fun updateUser(
        @Path("username") username: String,
        @Body completePayload: UpdateUserAPIModel
    ): Response<Unit>


    // Post routes
    @POST("posts")
    suspend fun createPost(@Body post: PostRequest): Response<PostAPIModel>

    @GET("posts")
    suspend fun getPosts(
        @Query("usernames") usernames: List<String>? = null,
        @Query("pageSize") pageSize: Int,
        @Query("pageToken") pageToken: String? = null,
        @Query("isUserFeed") isUserFeed: Boolean? = null
    ): Response<PostsResult>

    @POST("posts/{postId}/comments")
    suspend fun createComment(
        @Path("postId") postId: String,
        @Body comment: CommentPayload
    ): Response<Unit>

    @POST("posts/{postId}/likes")
    suspend fun addLike(@Path("postId") postId: String): Response<Unit>

    @DELETE("posts/{postId}/likes")
    suspend fun removeLike(@Path("postId") postId: String): Response<Unit>


    // AI generation routes
    @POST("generate/complete")
    suspend fun generateCompletion(@Body completePayload: AutoCompleteOptions): Response<LLMResult>

    @POST("generate/self-description")
    suspend fun createSelfDescription(@Body completePayload: AutoCompleteOptions): Response<LLMSelfDescription>

}