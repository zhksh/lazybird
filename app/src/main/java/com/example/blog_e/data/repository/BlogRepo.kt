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

    override suspend fun createPost(post: Post, params: AutogenrationOptions): ApiResult<PostAPIModel> {
        val postReq = PostRequest(post, params)
        return apiHandler.handleApi { backendS.createPost(postReq) }
    }

    override suspend fun completePost(completePayload: AutoCompleteOptions): ApiResult<LLMResult> {
        return apiHandler.handleApi { backendS.generateCompletion(completePayload) }
    }

    override suspend fun createComment(postId: String, content: String): ApiResult<Unit> {
        return apiHandler.handleApi {
            backendS.createComment(postId, CommentPayload(content=content))
        }
    }

    override suspend fun addLike(postId: String): ApiResult<Unit> {
        return apiHandler.handleApi {
            backendS.addLike(postId)
        }
    }

    override suspend fun removeLike(postId: String): ApiResult<Unit> {
        return apiHandler.handleApi {
            backendS.removeLike(postId)
        }
    }

    /**
     * let's define page size, page size is the only required param, rest is optional
     */
    fun getDefaultPageConfig(): PagingConfig {
        return PagingConfig(
            pageSize = Config.defaultPageSize,
            enablePlaceholders = Config.enablePlaceHolders,
            prefetchDistance = Config.prefetchDistance,
        )
    }

}