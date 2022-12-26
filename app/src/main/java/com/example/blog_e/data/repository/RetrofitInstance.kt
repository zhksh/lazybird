package com.example.blog_e.data.repository

import android.content.Context
import com.example.blog_e.utils.SessionManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit


class ApiClient {

    private lateinit var apiService: BlogEAPI

    fun getApiService(context: Context): BlogEAPI {

        // Initialize ApiService if not initialized yet
        if (!::apiService.isInitialized) {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okhttpClient(context)) // Add our Okhttp client
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
            .build()
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