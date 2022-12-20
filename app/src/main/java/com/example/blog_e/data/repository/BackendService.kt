package com.example.blog_e.data.repository

import com.example.blog_e.data.model.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path


const val BASE_URL = "http://localhost:5432/"

interface BackendService {

    @GET("users/{username}")
    fun getUser(@Path("username") user: String, ): Call<User>

}