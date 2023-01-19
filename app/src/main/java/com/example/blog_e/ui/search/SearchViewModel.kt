package com.example.blog_e.ui.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.blog_e.Config
import com.example.blog_e.data.model.GetUserAPIModel
import com.example.blog_e.data.repository.ApiError
import com.example.blog_e.data.repository.ApiException
import com.example.blog_e.data.repository.ApiSuccess
import com.example.blog_e.data.repository.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class SearchUIState(
    val query: String = "",
    val isSearching: Boolean = false
)

data class ResultUIState(
    val isSuccessful: Boolean = false,
    val isSearchStarted: Boolean = false,
    val userAPIModel: GetUserAPIModel? = null,
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val userRepo: UserRepo
) : ViewModel() {
    val TAG = Config.tag(this.toString())

    private val _text = MutableLiveData<String>().apply {
        value = "Recent searches"
    }
    val text: LiveData<String> = _text

    private val _searchUIState = MutableStateFlow(SearchUIState())
    val searchUIState: StateFlow<SearchUIState> = _searchUIState.asStateFlow()


    private val _resultUIState = MutableStateFlow(ResultUIState())
    val resultUIState: StateFlow<ResultUIState> = _resultUIState.asStateFlow()

    suspend fun searchUser() {
        Log.i(TAG, "searchUser: ${searchUIState.value.query}")
        startSearching()
        val res = userRepo.getUser(_searchUIState.value.query)
        stopSearching()
        when (res) {
            is ApiSuccess -> {
                updateCurrentUserResult(res.data)
                Log.i(TAG, "searchUser: war erflogreich")
            }
            is ApiError -> {
                revokeResultState()
                Log.i(TAG, "searchUser: war fehler mit Message ${res.message}")
            }
            is ApiException -> Log.i(TAG, "searchUser: hat einen Fehler geworfen")
        }
    }

    private fun startSearching() {
        _searchUIState.update {
            it.copy(
                isSearching = true
            )
        }
        _resultUIState.update {
            it.copy(
                isSearchStarted = true
            )
        }
    }

    private fun revokeResultState() {
        _resultUIState.update {
            it.copy(
                userAPIModel = null,
                isSuccessful = false,
            )
        }
    }

    fun clearLastResult() {
        if (_resultUIState.value.isSuccessful) {
            _resultUIState.update {
                it.copy(
                    userAPIModel = null,
                    isSuccessful = false,
                    isSearchStarted = false
                )
            }
        }
    }

    private fun updateCurrentUserResult(data: GetUserAPIModel) {
        _resultUIState.update {
            it.copy(
                userAPIModel = data,
                isSuccessful = true
            )
        }
    }

    fun updateQuery(query: String) {
        _searchUIState.update {
            it.copy(query = query)
        }
    }

    private fun stopSearching() {
        _searchUIState.update {
            it.copy(isSearching = false)
        }
    }
}