package com.example.blog_e.data.repository

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okio.Buffer
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import kotlin.system.measureTimeMillis

data class ErrorResponse(
    val message: String,
    val error: String
)
sealed interface ApiResult<T : Any>

class ApiSuccess<T : Any>(val data: T) : ApiResult<T>
class ApiError<T : Any>(val code: Int, val message: String?) : ApiResult<T>
class ApiException<T : Any>(val e: Throwable) : ApiResult<T>


class ApiHandler(private val tag: String) {

    suspend fun <T : Any> handleApi(execute: suspend () -> Response<T>): ApiResult<T> {
        return try {
            var response: Response<T>
            val timeInMillis = measureTimeMillis {
                response = execute()
            }
            Log.v(tag, "call took: $timeInMillis ms")
            val body = response.body()
            if (response.isSuccessful && body != null) {
                Log.v(tag, "response body: $body")
                ApiSuccess(body)
            }
            else {
                val errBody = response.errorBody()!!.charStream().readText()
                Log.e(tag,"API request unsuccessful. Error ${response.code()}: ${response.message()}: ${errBody.toString()}")
                ApiError(code = response.code(), message = errBody.toString())
            }

        } catch (e: HttpException) {
            Log.e(tag, "Could not fetch with http: " + e.message)
            ApiError(code = e.code(), message = e.message())
        } catch (e: IOException) {
            Log.e(tag, "IOException occurred: " + e.message)
            ApiException(e)
        } catch (e: Throwable) {
            Log.e(tag, "Exception occurred: " + e.message )
            ApiException(e)
        }
    }

}