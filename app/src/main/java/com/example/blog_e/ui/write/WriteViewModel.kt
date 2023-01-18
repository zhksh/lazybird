package com.example.blog_e.ui.write


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
    val errorMsg: String = "",
    val success: Boolean = false,
    val running: Boolean = false
    )

data class GeneratePostState(
    val success: Boolean = false,
    val generatedText: String = "",
)


@HiltViewModel
class WriteViewModel @Inject constructor(private val postRepo: BlogRepo) : ViewModel() {

    private val TAG = Config.tag(this.toString())

    private var _uiState = MutableStateFlow(WriteUiState())
    var uiState: StateFlow<WriteUiState> = _uiState.asStateFlow()

    private var _generatePostState = MutableStateFlow(GeneratePostState())
    var generateState: StateFlow<GeneratePostState> = _generatePostState.asStateFlow()


    fun createPost(data: Post) = viewModelScope.launch {
        _uiState.update { it.copy( running = true, errorMsg = "") }
       val res =  postRepo.createPost(data)
        _uiState.update { it.copy( running = false) }
        when (res) {
            is ApiException -> {
                _uiState.update { it.copy(errorMsg = res.e.message!!, success = false) }
            }
            is ApiError -> {
                _uiState.update { it.copy(errorMsg = "Api not callable, try tomorrow or so", success = false) }
            }
            is ApiSuccess -> {
                _uiState.update {it.copy(success = true)}
            }
        }
    }

    fun completePost(data: CompletePayload){
        viewModelScope.launch {
            _uiState.update { it.copy( running = true, errorMsg = "") }
            val res = postRepo.completePost(data)
            _uiState.update { it.copy( running = false) }
            when (res) {
                is ApiException -> {
                    _uiState.update { it.copy(errorMsg = res.e.message!!, success = false) }
                }
                is ApiError -> {
                    _uiState.update { it.copy(errorMsg = "Api not callable, try tomorrow or so", success = false) }
                }
                is ApiSuccess -> {
                    if (res.data.response.isBlank()) _uiState.update {
                        it.copy(success = false, errorMsg = "Empty response try later")}
                    else {
                        _uiState.update { it.copy(errorMsg = "") }
                        _generatePostState.update { it.copy(generatedText = res.data.response, success = true) }
                    }
                }
            }
        }
    }




}