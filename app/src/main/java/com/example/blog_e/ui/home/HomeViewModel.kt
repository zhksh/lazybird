package com.example.blog_e.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.liveData
import com.example.blog_e.data.model.GetPostsQueryModel
import com.example.blog_e.data.model.PostAPIModel
import com.example.blog_e.data.repository.BlogRepo
import com.example.blog_e.data.repository.PostPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class HomeState(
    val isNotUserFeed: Boolean = false,
    val isRefreshingPosts: Boolean = false,
    //TODO: das hier irgendwie benutzen
    val isChangingFeed: Boolean = false,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val blogRepo: BlogRepo
) : ViewModel() {

    private val _homeState = MutableStateFlow(HomeState())
    val homeState = _homeState.asStateFlow()

    private val _posts = MutableLiveData<PagingData<PostAPIModel>>()

    fun getPosts(): LiveData<PagingData<PostAPIModel>> {
        val liveDate = Pager(
            blogRepo.getDefaultPageConfig(),
        ) {
            blogRepo
            PostPagingSource(
                blogRepo, GetPostsQueryModel(
                    listOf(),
                    5,
                    null,
                    // TODO: isUserFeed richtig updaten!!! Ggf. eine Methode "fetch(), die das LiveData Objekt neusetzt."
                    isUserFeed = homeState.value.isNotUserFeed
                )
            )
        }.liveData
            .cachedIn(viewModelScope)
        _posts.value = liveDate.value
        return liveDate
    }

    fun onClickUserFeed(isGlobal: Boolean) {
        _homeState.update {
            it.copy(isNotUserFeed = isGlobal)
        }
    }

    fun refreshPosts(isRefreshing: Boolean) {
        _homeState.update {
            it.copy(isRefreshingPosts = isRefreshing)
        }
    }

}

