package com.example.blog_e.ui.profile

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class ProfileUiState(
    val errMsg: String = "",
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val blogRepo: BlogRepo,
) : ViewModel() {

    private var _profileUiState = MutableStateFlow(ProfileUiState())
    var profileUiState: StateFlow<ProfileUiState> = _profileUiState.asStateFlow()

    private var _posts: MutableLiveData<PagingData<PostAPIModel>> =
        Pager(
            blogRepo.getDefaultPageConfig(),
        ) {
            PostPagingSource(
                blogRepo,
                isUserFeed = false,
                usernames = listOf(DEFAULT_USER)
            )
        }.liveData.cachedIn(viewModelScope) as MutableLiveData<PagingData<PostAPIModel>>


    var posts: LiveData<PagingData<PostAPIModel>> = _posts

    companion object {
        const val DEFAULT_USER = "me"
    }
}
