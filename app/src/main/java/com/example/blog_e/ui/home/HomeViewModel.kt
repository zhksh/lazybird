package com.example.blog_e.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blog_e.data.model.PostsRequest
import com.example.blog_e.data.repository.BlogRepo
import com.example.blog_e.data.repository.UserRepo
import com.example.blog_e.models.PostsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepo: UserRepo,
    private val blogRepo: BlogRepo
) : ViewModel() {

    private val _posts: MutableLiveData<List<PostsViewModel>> = MutableLiveData()

    fun getPosts(): LiveData<List<PostsViewModel>> {
        return _posts
    }

    fun fetchBlogs(isUserFeed: Boolean) {
        val req = PostsRequest(
            isUserFeed,
            25,
            ""
        )
        viewModelScope.launch {
            blogRepo.getPosts(req)
            println("gelauncht!")
        }
    }


}