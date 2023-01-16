package com.example.blog_e.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.blog_e.data.model.GetPostsQueryModel
import com.example.blog_e.data.model.PostAPIModel

class PostPagingSource(
    val blogRepo: BlogRepo,
    private val postsQueryModel: GetPostsQueryModel
) : PagingSource<String, PostAPIModel>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, PostAPIModel> {
        val nextPageToken = params.key
        print(params.loadSize)
        println(nextPageToken)
        // TODO: überlegen, ob das als Objekt erstellt werden soll oder Query für query eingegeben wird
        val newParameters = GetPostsQueryModel(
            postsQueryModel.usernames,
            postsQueryModel.pageSize,
            nextPageToken,
            postsQueryModel.isUserFeed
        )
        val res = blogRepo.getPosts(newParameters)
        when (res) {
            is ApiSuccess -> return LoadResult.Page(
                data = res.data.posts,
                prevKey = null,  // only paging forward
                nextKey = res.data.nextPageToken
            )
            is ApiException -> return LoadResult.Error(res.e)

            is ApiError -> return LoadResult.Error(Exception(res.message))
        }
    }

    override fun getRefreshKey(state: PagingState<String, PostAPIModel>): String? {
        return state.anchorPosition?.let { state.closestItemToPosition(it)?.id }
    }
}