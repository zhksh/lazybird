package com.example.blog_e.ui.home

import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.liveData
import com.example.blog_e.data.model.PostAPIModel
import com.example.blog_e.data.repository.BlogRepo
import com.example.blog_e.data.repository.PostPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _isUserFeed = MutableLiveData(IS_USER_FEED_STARTING_PAGE)

    val isUserFeed: LiveData<Boolean> = _isUserFeed

    // TODO: home state aktuell nicht benutzt
    private val _homeState = MutableStateFlow(HomeState())
    val homeState = _homeState.asStateFlow()

    private var _posts: MutableLiveData<PagingData<PostAPIModel>> = _isUserFeed.switchMap {
        Pager(
            blogRepo.getDefaultPageConfig(),
        ) {
            PostPagingSource(
                blogRepo,
                isUserFeed = it,
                usernames = listOf()
            )
        }.liveData
            .cachedIn(viewModelScope)
    } as MutableLiveData<PagingData<PostAPIModel>>

    var posts: LiveData<PagingData<PostAPIModel>> = _posts


    fun onClickUserFeed(isGlobal: Boolean) {
        _isUserFeed.value = !isGlobal
    }

    fun refreshPosts(isRefreshing: Boolean) {
//        _homeState.update {
//            it.copy(isRefreshingPosts = isRefreshing)
//        }
    }

    companion object {
        const val IS_USER_FEED_STARTING_PAGE = false
    }

}

