package com.example.blog_e.ui.write


import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blog_e.Config
import com.example.blog_e.data.model.CompletePayload
import com.example.blog_e.data.model.Post
import com.example.blog_e.data.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WriteUiState(
    val isAIMode: Boolean = false,
    val postInput: String = "",
    val userMessage: String? = null,
    val postSuccesful: Boolean = false,
    val generatedText: String = "",
    val error: String = "",
    val success: Boolean = false,
    val running: Boolean = false
)


@HiltViewModel
class WriteViewModel @Inject constructor(private val postRepo: BlogRepo) : ViewModel() {

    private val TAG = Config.tag(this.toString())

    private var _uiState = MutableStateFlow(WriteUiState())
    var uiState: StateFlow<WriteUiState> = _uiState.asStateFlow()


    fun createPost(data: Post) = viewModelScope.launch {

       val res =  postRepo.createPost(data)
        when (res) {
            is ApiException -> {
                updateErr("error: ${res.e.message}")}
            is ApiError -> {
                updateErr("this didnt work, try later")
            }
            is ApiSuccess -> {
                _uiState.update {
                    it.copy(postSuccesful = true)
                }
            }
        }

    }

    fun completePost(data: CompletePayload){
        viewModelScope.launch {
            updateRunning(true)
            val res = postRepo.completePost(data)
            when (res) {
                is ApiException -> {
                    updateErr("error: ${res.e.message}")}
                is ApiError -> {
                    updateErr("this didnt work, try later")
                }
                is ApiSuccess -> {
                    if (res.data.response.isBlank()) updateErr("empty response, try longer prompt")
                    else updatePostText(res.data.response)
                }
            }
            updateRunning(false)
        }
    }

    fun updatePostText(newText: String) {
        _uiState.update {
            it.copy(postInput = newText, success = true)
        }
    }

    fun updateRunning(running: Boolean) {
        _uiState.update {
            it.copy(running = running)
        }
    }

    fun updateErr(errStr: String) {
        _uiState.update {
            it.copy(error = errStr, success = false)
        }
    }


}