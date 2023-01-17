package com.example.blog_e.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.blog_e.Config
import com.example.blog_e.data.model.*
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class BlogRepo(private val backendS: BlogEAPI) : BlogPostRepository {

    private val apiHandler: ApiHandler = ApiHandler(Config.tag(this.toString()))

    override fun getPostsStream(): LiveData<List<Post>> {
        TODO("Not yet implemented")
    }

    /**
     * Includes mapping. Consider refactoring this into a domain layer
     */
    override suspend fun getPosts(
        usernames: List<String>?,
        pageSize: Int,
        pageToken: String?,
        isUserFeed: Boolean
    ): ApiResult<PostsResult> {
        return apiHandler.handleApi {
            backendS.getPosts(
                usernames,
                pageSize,
                pageToken,
                isUserFeed,
            )
        }
    }

    override suspend fun refreshPosts() {
        TODO("Not yet implemented")
    }

    override fun getPostStream(): ApiResult<LiveData<Post>> {
        TODO("Not yet implemented")
    }

    override suspend fun getPost(post: Post): ApiResult<Post> {
        TODO("Not yet implemented")
    }

    override suspend fun createPost(post: Post): ApiResult<PostAPIModel> {
        val postReq = PostRequest(
            post.content,
            "happy",
            true,
            shouldAutoComplete = false,
            0
        )
        return apiHandler.handleApi { backendS.createPost(postReq) }
    }

    override suspend fun completePost(completePayload: CompletePayload): ApiResult<LLMResult> {
        return apiHandler.handleApi {backendS.generateCompletion(completePayload)}
    }

    override fun getCommentsStream(): ApiResult<LiveData<List<Comment>>> {
        TODO("Not yet implemented")
    }

    override suspend fun getComments(post: Post): ApiResult<List<Comment>> {
        TODO("Not yet implemented")
    }

    override suspend fun createComment(comment: Comment, post: Post): ApiResult<Any> {
        TODO("Not yet implemented")
    }

    override suspend fun likeOrUnlikePost(like: Like, post: Post): ApiResult<Any> {
        TODO("Not yet implemented")
    }
}