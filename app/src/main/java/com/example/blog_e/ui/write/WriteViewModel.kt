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
    val isPostSaved: Boolean = false,
    val generatedText: String = "",
    val error: String = "",
    val success: Boolean = false,
    val running: Boolean = false
)


@HiltViewModel
class WriteViewModel @Inject constructor(private val postRepo: BlogRepo) : ViewModel() {

    private val TAG = Config.tag(this.toString())

    // TODO: ui state richtig verwenden
    private val _uiState = MutableStateFlow(WriteUiState())
    val uiState: StateFlow<WriteUiState> = _uiState.asStateFlow()

    private val _postText = MutableLiveData<String>()
    val postText: LiveData<String> = _postText


    fun hideAiViews() {
    }

    // Called when clicking on create button
    fun savePost() {

        if (uiState.value.postInput.isBlank()) {
            _uiState.update {
                it.copy(userMessage = "Task cannot be empty")
            }
            return
        }

        createPost()
    }

    fun createPost() = viewModelScope.launch {
        // TODO: save in service
        _uiState.update {
            it.copy(isPostSaved = true)
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