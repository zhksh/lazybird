package com.example.blog_e.ui.write


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blog_e.Config
import com.example.blog_e.data.model.AutoCompleteOptions
import com.example.blog_e.data.model.AutogenrationOptions
import com.example.blog_e.data.model.Post
import com.example.blog_e.data.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


data class GeneratePostResponse (
    val generatedText: String = "",
    val errResponse: SuccessResponse? = null
)


@HiltViewModel
class WriteViewModel @Inject constructor(private val postRepo: BlogRepo) : ViewModel() {

    private val TAG = Config.tag(this.toString())

    fun createPost(data: Post, params: AutogenrationOptions): LiveData<SuccessResponse> {
        val response = MutableLiveData<SuccessResponse>()
        viewModelScope.launch {
            val res = postRepo.createPost(data, params)
            when(res){
                is ApiSuccess ->  {
                    response.value = SuccessResponse(null)
                }
                is ApiError ->{
                    Log.e(TAG, "sending post failed: ${res} ")
                    response.value = SuccessResponse("Posting failed, try tommorrow or after the weekend")
                }
                is ApiException -> {
                    Log.e(TAG, "sending post failed: ${res} ")
                    response.value = SuccessResponse("posting failed, try tommorrow or after the weekend")
                }
            }
        }
        return response
    }

    fun completePost(data: AutoCompleteOptions): LiveData<GeneratePostResponse> {
        val response = MutableLiveData<GeneratePostResponse>()
        viewModelScope.launch {
            val res = postRepo.completePost(data)
            when(res){
                is ApiSuccess ->  {
                    response.value = GeneratePostResponse(generatedText = res.data.response)
                }
                is ApiError ->{
                    Log.e(TAG, "sending post failed: ${res} ")
                    response.value =
                        GeneratePostResponse(errResponse =
                            SuccessResponse("posting failed, try tommorrow or after the weekend"))
                }
                is ApiException -> {
                    Log.e(TAG, "sending post failed: ${res} ")
                    response.value =
                        GeneratePostResponse(errResponse =
                        SuccessResponse("posting failed, try tommorrow or after the weekend"))
                }
            }
        }
        return response
    }





}