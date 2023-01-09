package com.example.blog_e.ui.write

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WriteUiState(
    val isAIMode: Boolean = false,
    val postInput: String = "",
    val userMessage: String? = null,
    val isPostSaved: Boolean = false
)

class WriteViewModel : ViewModel() {

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
//        Log.i()
        _uiState.update {
            it.copy(isPostSaved = true)
        }
    }

    fun updatePostText(newText: String) {
        _uiState.update {
            it.copy(postInput = newText)
        }
    }

}