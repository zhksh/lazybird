package com.example.blog_e.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagingConfig
import com.example.blog_e.Config
import com.example.blog_e.data.model.*

class BlogRepo(private val backendS: BlogEAPI) : BlogPostRepository {

    private val apiHandler: ApiHandler = ApiHandler(Config.tag(this.toString()))

    override fun getPostsStream(): LiveData<List<Post>> {
        TODO("Not yet implemented")
    }

    /**
     * Includes mapping. Consider refactoring this into a domain layer
     */
    override suspend fun getPosts(postsQueryModel: GetPostsQueryModel): ApiResult<PostsResult> {
        return apiHandler.handleApi {
            backendS.getPosts(
                postsQueryModel.usernames,
                postsQueryModel.pageSize,
                postsQueryModel.pageToken,
                postsQueryModel.isUserFeed,
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
        val post = PostRequest(
            post.content,
            "happy",
            true,
            shouldAutoComplete = false,
            0
        )
        return apiHandler.handleApi { backendS.createPost(post) }
//        val response: Response<PostAPIModel> = try {
//            backendS.createPost(
//                PostRequest(
//                    post.content,
//                    "happy",
//                    true,
//                    shouldAutoComplete = false,
//                    0
//                )
//            )
//        } catch (e: IOException) {
//            Log.e(this.toString(), "Keine buffer")
//            return
//        } catch (e: HttpException) {
//            Log.e(this.toString(), "Keine Verbindung bekommen")
//            return
//        }
//
//        if (response.isSuccessful && response.body() != null) {
//            Log.e(this.toString(), "erfolgreich")
//        } else {
//            Log.e(this.toString(), "Not successful")
//        }

    }

    override suspend fun completePost(completePayload: CompletePayload): ApiResult<LLMResult> {
        return apiHandler.handleApi { backendS.generateCompletion(completePayload) }
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

    /**
     * let's define page size, page size is the only required param, rest is optional
     */
    fun getDefaultPageConfig(): PagingConfig {
        return PagingConfig(pageSize = DEFAULT_PAGE_SIZE, enablePlaceholders = false)
    }

    companion object {
        const val DEFAULT_PAGE_SIZE = 25
    }
}