package com.example.blog_e.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.blog_e.data.model.*
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class BlogRepo: BlogPostRepository{

    private var backendS: BlogEAPI

    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        backendS = retrofit.create(BlogEAPI::class.java)
    }

    override fun getPostsStream(): LiveData<List<Post>> {
        TODO("Not yet implemented")
    }

    override suspend fun getPosts(): List<Post> {
        TODO("Not yet implemented")
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
                    post.autogenerateResponses,
                    shouldAutoComplete = false,
                    0
                )
            )
        } catch (e: IOException ) {
            Log.e(this.toString(), "Keine buffer")
            return
        } catch (e: HttpException) {
            Log.e(this.toString(), "Keine Verbindung bekommen")
            return
        }

        if (response.isSuccessful && response.body()!=null) {
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