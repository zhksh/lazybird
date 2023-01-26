package com.example.blog_e.data.repository

import com.example.blog_e.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface BlogEAPI {

    // User
    @POST("users")
    suspend fun signUp(@Body params: NewUserAPIModel): Response<Authorization>

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

    @POST("generate/self-descpription")
    suspend fun createSelfDescription(@Body completePayload: CompletePayload): Response<LLMSelfDescription>
    // Post
    @POST("posts")
    suspend fun createPost(@Body post: PostRequest): Response<PostAPIModel>

    @GET("posts")
    suspend fun getPosts(
        @Query("usernames") usernames: List<String>? = null,
        @Query("pageSize") pageSize: Int,
        @Query("pageToken") pageToken: String? = null,
        @Query("isUserFeed") isUserFeed: Boolean? = null,

        ): Response<PostsResult>

    @POST("users/{username}")
    suspend fun updateUser(@Path("username") username: String, @Body completePayload: UpdateUserAPIModel): Response<Unit>

    @POST("generate/complete")
    suspend fun generateCompletion(@Body completePayload: CompletePayload): Response<LLMResult>

    @POST("posts/{postId}/comments")
    suspend fun createComment(@Path("postId") postId: String, @Body comment: CommentPayload): Response<Unit>

    @POST("posts/{postId}/likes")
    suspend fun addLike(@Path("postId") postId: String): Response<Unit>

    @DELETE("posts/{postId}/likes")
    suspend fun removeLike(@Path("postId") postId: String): Response<Unit>


    /*TODO: APIs for
       (WS) Watch post  http://localhost:6969/posts/{id}/watch
       Like/Unlike post http://localhost:6969/posts/{id}/likes
       New comment      http://localhost:6969/posts/{id}/comments
     */

}