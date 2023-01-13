package com.example.blog_e.ui.post

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.example.blog_e.data.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PostThreadViewModel @Inject constructor(
    private val blogRepo: BlogRepo,
) : ViewModel() {

    val postId = ObservableField<String>();
}