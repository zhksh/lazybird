package com.example.blog_e.ui.write

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WriteViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is write Fragment"
    }
    val text: LiveData<String> = _text
}