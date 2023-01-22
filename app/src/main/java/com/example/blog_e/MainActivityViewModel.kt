package com.example.blog_e

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blog_e.data.model.User
import com.example.blog_e.data.model.mapApiUser
import com.example.blog_e.data.repository.*
import com.example.blog_e.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MainActivityState(
    val user: User? = null,
)

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val userRepo: UserRepo,
    private val sessionManager: SessionManager,
) : ViewModel() {
    private val tag = Config.tag(this.toString())
    private val _uiState = MutableStateFlow(MainActivityState())

    val isLoading = ObservableBoolean(true)
    val uiState = _uiState.asStateFlow()

    init {
        if (sessionManager.fetchAuthToken() == null) {
            _uiState.update {
                it.copy(user = null)
            }
        } else {
            viewModelScope.launch {
                getUser()
            }
        }
    }

    private suspend fun getUser() {
        when (val result = userRepo.getUser("me")) {
            is ApiSuccess -> {
                val user = mapApiUser(result.data)
                _uiState.update {
                    it.copy(user = user)
                }
            }
            is ApiError -> {
                sessionManager.resetSession()
                _uiState.update {
                    it.copy(user = null)
                }
            }
            is ApiException -> {
                sessionManager.resetSession()
                _uiState.update {
                    it.copy(user = null)
                }
            }
        }
    }
}