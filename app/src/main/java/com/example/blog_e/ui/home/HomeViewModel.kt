package com.example.blog_e.ui.home

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
import com.example.blog_e.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    sessionManager: SessionManager,
    private val blogRepo: BlogRepo
) : ViewModel() {
    val username = sessionManager.getUsername()

    fun getPosts(isUserFeed: Boolean): LiveData<PagingData<PostAPIModel>> {
        return Pager(blogRepo.getDefaultPageConfig()) {
            PostPagingSource(
                blogRepo,
                isUserFeed = isUserFeed,
                usernames = emptyList()
            )
        }.liveData.cachedIn(viewModelScope) as MutableLiveData<PagingData<PostAPIModel>>
    }
}

