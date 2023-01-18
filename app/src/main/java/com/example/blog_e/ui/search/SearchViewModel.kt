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
    val userAPIModel: GetUserAPIModel? = null
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
//        clearLastResult()
        Log.i(TAG, "searchUser: ${searchUIState.value.query}")

        val res = userRepo.getUser(_searchUIState.value.query)
        when (res) {
            is ApiSuccess -> {
                updateCurrentUserResult(res.data)
                Log.i(TAG, "searchUser: war erflogreich")
            }
            is ApiError -> {

                Log.i(TAG, "searchUser: war fehler")
            }
            is ApiException -> Log.i(TAG, "searchUser: hat einen Fehler geworfen")
        }
    }

    private fun clearLastResult() {
        if (_resultUIState.value.isSuccessful) {
            _resultUIState.update {
                it.copy(
                    userAPIModel = null,
                    isSuccessful = false
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

}