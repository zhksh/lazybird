package com.example.blog_e.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.blog_e.data.model.*
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class BlogRepo(private val backendS: BlogEAPI) : BlogPostRepository {

    private val apiHandler: ApiHandler = ApiHandler(this.toString())

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

    override fun getPostStream(): LiveData<Post> {
        TODO("Not yet implemented")
    }

    override suspend fun getPost(post: Post): Post {
        TODO("Not yet implemented")
    }

    override suspend fun createPost(post: Post) {
        val response: Response<PostAPIModel> = try {
            backendS.createPost(
                PostRequest(
                    post.content,
                    "happy",
                    true,
                    shouldAutoComplete = false,
                    0
                )
            )
        } catch (e: IOException) {
            Log.e(this.toString(), "Keine buffer")
            return
        } catch (e: HttpException) {
            Log.e(this.toString(), "Keine Verbindung bekommen")
            return
        }

        if (response.isSuccessful && response.body() != null) {
            Log.e(this.toString(), "erfolgreich")
        } else {
            Log.e(this.toString(), "Not successful")
        }

    }

    override fun getCommentsStream(): LiveData<List<Comment>> {
        TODO("Not yet implemented")
    }

    override suspend fun getComments(post: Post): List<Comment> {
        TODO("Not yet implemented")
    }

    override suspend fun createComment(comment: Comment, post: Post) {
        TODO("Not yet implemented")
    }

    override suspend fun likeOrUnlikePost(like: Like, post: Post) {
        TODO("Not yet implemented")
    }
}