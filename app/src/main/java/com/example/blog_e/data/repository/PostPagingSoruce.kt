package com.example.blog_e.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.blog_e.data.model.GetPostsQueryModel
import com.example.blog_e.data.model.PostAPIModel

class PostPagingSource(
    val blogRepo: BlogRepo,
    private val isUserFeed: Boolean,
    private val usernames: List<String>
) : PagingSource<String, PostAPIModel>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, PostAPIModel> {
        val queryParamter = GetPostsQueryModel(
            usernames,
            params.loadSize,
            params.key,
            isUserFeed
        )
        val res = blogRepo.getPosts(queryParamter)
        return when (res) {
            is ApiSuccess -> {
                val nextPageToken: String? = res.data.nextPageToken.ifEmpty { null }
                LoadResult.Page(
                    data = res.data.posts,
                    prevKey = null,  // only paging forward
                    nextKey = nextPageToken
                )
            }
            is ApiException -> LoadResult.Error(res.e)

            is ApiError -> LoadResult.Error(Exception(res.message))
        }
    }

    override fun getRefreshKey(state: PagingState<String, PostAPIModel>): String? {
        return state.anchorPosition?.let { state.closestItemToPosition(it)?.id }
    }
}