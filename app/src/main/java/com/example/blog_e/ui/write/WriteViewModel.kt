package com.example.blog_e.ui.write

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blog_e.data.model.CompletePayload
import com.example.blog_e.data.repository.ApiClient
import com.example.blog_e.data.repository.LLMResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Response

data class WriteUiState(
    val isAIMode: Boolean = false,
    val postInput: String = "",
    val userMessage: String? = null,
    val isPostSaved: Boolean = false
)

class WriteViewModel : ViewModel() {
    private val TAG = "writemodel"
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

    fun completePost(data: CompletePayload, context: Context){
        Log.i(TAG, "before coroutinge")
        viewModelScope.launch {
            try {
                val client = ApiClient.getClient(context)
                Toast.makeText(
                    context,
                    "Generating ..",
                    Toast.LENGTH_LONG
                ).show()
                val resp = client.generateCompletion(data)
                if (resp.isSuccessful()) {
                    _uiState.value = _uiState.value.copy(postInput = resp.message())
                    Log.i(TAG, resp.body().toString())

                } else {
                    Toast.makeText(
                        context,
                        resp.errorBody().toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }catch (Ex:Exception){
                Log.e("Error",Ex.localizedMessage)
            }
        }





    }

    fun updatePostText(newText: String) {
        _uiState.update {
            it.copy(postInput = newText)
        }
    }

}