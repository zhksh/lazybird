package com.example.blog_e.data.repository

import android.util.Log
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

sealed interface ApiResult<T : Any>

class ApiSuccess<T : Any>(val data: T) : ApiResult<T>
class ApiError<T : Any>(val code: Int, val message: String?) : ApiResult<T>
class ApiException<T : Any>(val e: Throwable) : ApiResult<T>


class ApiHandler(private val tag: String) {

    suspend fun <T : Any> handleApi(
        execute: suspend () -> Response<T>
    ): ApiResult<T> {
        return try {
            val response = execute()
            val body = response.body()
            if (response.isSuccessful && body != null) {
                ApiSuccess(body)
            } else {
                Log.e(
                    tag,
                    "API request unsuccessful. Error ${response.code()}: ${response.message()}"
                )
                ApiError(code = response.code(), message = response.message())
            }
        } catch (e: HttpException) {
            Log.e(tag, "Could not fetch with http")
            ApiError(code = e.code(), message = e.message())
        } catch (e: IOException) {
            Log.e(tag, "IOException occurred")
            ApiException(e)
        } catch (e: Throwable) {
            Log.e(tag, "Exception occurred")
            ApiException(e)
        }
    }

}