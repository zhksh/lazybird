package com.example.blog_e.ui.VisitProfile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.liveData
import com.example.blog_e.data.model.PostAPIModel
import com.example.blog_e.data.repository.BlogRepo
import com.example.blog_e.data.repository.PostPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class ProfileVisitViewModel @Inject constructor(
    private val blogRepo: BlogRepo,
) : ViewModel() {

    var username: String? = null


    private var _posts: MutableLiveData<PagingData<PostAPIModel>> = MutableLiveData()

    val posts: LiveData<PagingData<PostAPIModel>> = _posts


    fun initPager(username: String) {
        _posts = Pager(
            blogRepo.getDefaultPageConfig(),
        ) {
            PostPagingSource(
                blogRepo,
                isUserFeed = false,
                usernames = listOf(username)
            )
        }.liveData.cachedIn(viewModelScope) as MutableLiveData<PagingData<PostAPIModel>>
    }

    fun fetchUser(currentUser: String) {

    }
}