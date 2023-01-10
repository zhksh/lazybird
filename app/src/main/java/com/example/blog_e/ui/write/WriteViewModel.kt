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
    val isPostSaved: Boolean = false
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
            val res = postRepo.completePost(data)
            when (res) {
                is ApiException -> {Log.e(TAG, "network error")}
                is ApiError -> {Log.e(TAG, res.message.toString() ) }
                is ApiSuccess -> {
                    _uiState.value = _uiState.value.copy(postInput = res.data.response)
                }
            }
        }
    }

    fun updatePostText(newText: String) {
        _uiState.update {
            it.copy(postInput = newText)
        }
    }

}