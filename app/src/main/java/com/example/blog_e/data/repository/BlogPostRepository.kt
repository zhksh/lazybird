package com.example.blog_e.data.repository

import com.example.blog_e.data.model.*

interface BlogPostRepository {

    suspend fun getPosts(postsQueryModel: GetPostsQueryModel): ApiResult<PostsResult>

    suspend fun createPost(post: Post, params: AutogenrationOptions): ApiResult<PostAPIModel>

    suspend fun completePost(completePayload: AutoCompleteOptions): ApiResult<LLMResult>

    suspend fun createComment(postId: String, content: String): ApiResult<Unit>

    suspend fun addLike(postId: String): ApiResult<Unit>

    suspend fun removeLike(postId: String): ApiResult<Unit>
}
