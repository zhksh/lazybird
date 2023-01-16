package com.example.blog_e.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.blog_e.data.model.GetPostsQueryModel
import com.example.blog_e.data.model.PostAPIModel
import com.example.blog_e.data.repository.*
import com.example.blog_e.models.PostsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

data class HomeState(
    val posts: List<PostsViewModel> = listOf(),
    val isGlobal: Boolean = false,
    val paginationToken: String? = null,
    val hasNextPage: Boolean = false,
    val isFetchingPosts: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val blogRepo: BlogRepo
) : ViewModel() {


    private val _posts: MutableLiveData<List<PostsViewModel>> = MutableLiveData()

    fun getPosts(): LiveData<List<PostsViewModel>> {
        return _posts
    }

    val flow: Flow<PagingData<PostAPIModel>> = Pager(
        blogRepo.getDefaultPageConfig()
    ) {
        PostPagingSource(
            blogRepo, GetPostsQueryModel(
                listOf(),
                30,
                null,
                isUserFeed = false
            )
        )
    }.flow.cachedIn(viewModelScope)


    private var isUserFeed = true
    private var hasNextPage = false
    private var nextPageToken: String = ""

    suspend fun fetchBlogs(
        isUserFeed: Boolean,
//        fetchFromStart: Boolean,
        pageSize: Int = 20,
        pageToken: String? = null
    ): ArrayList<PostAPIModel> {
        //TODO: fetch global instead of me, add flag for followers
        val postsQueryModel = GetPostsQueryModel(
            pageSize = pageSize,
            isUserFeed = isUserFeed,
            pageToken = pageToken,
        )
        val postsResult = blogRepo.getPosts(postsQueryModel)

        val flow: Flow<PagingData<PostAPIModel>> = Pager(
            PagingConfig(pageSize = 25)
        ) {
            PostPagingSource(blogRepo, postsQueryModel)
        }.flow.cachedIn(viewModelScope)


        return when (postsResult) {
            is ApiSuccess -> {
                // TODO: set next page token / store it
                nextPageToken = postsResult.data.nextPageToken
                hasNextPage = nextPageToken.isEmpty()
                println(nextPageToken)


                postsResult.data.posts
            }
            is ApiError -> {
                Log.e(this.toString(), "Wähhhhhh") // do something
                arrayListOf()
            }
            is ApiException -> throw postsResult.e
        }
    }

    fun onClickUserFeed() {

    }
}

