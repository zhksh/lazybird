package com.example.blog_e.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.blog_e.data.model.*
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.time.Instant
import java.util.*

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
    ): List<Post> {
        val postsApiResult = apiHandler.handleApi {
            backendS.getPosts(
                usernames,
                pageSize,
                pageToken,
                isUserFeed,
            )
        }
        println(usernames)
        when (postsApiResult) {
            is ApiSuccess -> {
                val postsResult = postsApiResult.data
                val posts = postsResult.posts.map {
                    Post(
                        id = UUID.fromString(it.id),
                        content = it.content,
                        publicationDate = Date.from(Instant.parse((it.timestamp))),
                        commentCount = it.commentCount,
                    )
                }

                return posts
            }
            is ApiError -> return listOf() // do something
            is ApiException -> throw postsApiResult.e
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