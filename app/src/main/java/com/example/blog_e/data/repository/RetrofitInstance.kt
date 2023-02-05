package com.example.blog_e.data.repository

import android.content.Context
import android.util.Log
import com.example.blog_e.Config
import com.example.blog_e.data.api.BlogEAPI
import com.example.blog_e.utils.SessionManager
import com.google.gson.GsonBuilder
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
            val gson = GsonBuilder().setDateFormat(Config.dateFormat).setLenient().create()
            val retrofit = Retrofit.Builder()
                .baseUrl(Config.apiAddress)
                .client(okhttpClient(context))
                .addConverterFactory(GsonConverterFactory.create(gson))
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
            .addInterceptor(CustomInterceptor())
            .readTimeout(Config.clientTimout.toLong(), TimeUnit.SECONDS)
            .connectTimeout(Config.clientTimout.toLong(), TimeUnit.SECONDS)
            .build()
    }


    class CustomInterceptor() : Interceptor {

        override fun intercept(chain: Interceptor.Chain): Response {
            val tag = Config.tag("custom interceptor")
            val req: Request = chain.request()
            val requestBuilder: Request.Builder = req.newBuilder()
            requestBuilder.header("Content-Type", "application/json")
            Log.d(tag, "url: ${req.url()}")
            //log body
            val buffer = Buffer()
            req.body()?.writeTo(buffer)

            Log.d(tag, "request body: $buffer")
            Log.d(tag, "headers: ${req.headers()}")

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
}
