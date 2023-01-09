package com.example.blog_e.data.repository

import android.content.Context
import android.util.Log
import com.example.blog_e.utils.SessionManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiClient {

    private lateinit var apiService: BlogEAPI

    fun getApiService(context: Context): BlogEAPI {

        // Initialize ApiService if not initialized yet
        if (!::apiService.isInitialized) {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okhttpClient(context))
                .addConverterFactory(GsonConverterFactory.create())// Add our Okhttp client
                .build()

            apiService = retrofit.create(BlogEAPI::class.java)
        }

        return apiService
    }


    /**
     * Initialize OkhttpClient with our interceptor
     */
    private fun okhttpClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context))
            .addInterceptor(CustomInterceptor(context))
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    companion object {
//        const val BASE_URL = "http://10.0.2.2:6969"
        const val BASE_URL = "https://mvsp-api.ncmg.eu"
    }
}





class CustomInterceptor(context: Context) : Interceptor {

    private val sessionManager = SessionManager(context)

    override fun intercept(chain: Interceptor.Chain): Response {
        val tag = "custom interceptor"
        val req: Request = chain.request()
        val requestBuilder: Request.Builder = req.newBuilder()
        requestBuilder.header("Content-Type", "application/json");
        //log body
        val buffer = Buffer()
        req.body()?.writeTo(buffer)
        Log.d(tag, "request body: ${buffer.toString()}")


        Log.d(tag, req.url().toString())
        Log.d(tag, req.headers().toString())

        return chain.proceed(requestBuilder.build())
    }


}
class AuthInterceptor(context: Context) : Interceptor {

    private val sessionManager = SessionManager(context)

    override fun intercept(chain: Interceptor.Chain): Response {
        val req: Request = chain.request()

        val requestBuilder: Request.Builder = req.newBuilder()

        sessionManager.fetchAuthToken()?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }

        return chain.proceed(requestBuilder.build())
    }


}