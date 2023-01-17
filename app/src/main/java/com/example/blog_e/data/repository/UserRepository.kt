package com.example.blog_e.data.repository

import androidx.lifecycle.LiveData
import com.example.blog_e.data.model.*

interface UserRepository {

    suspend fun signUp(user: NewUserAPIModel): ApiResult<Authorization>

    suspend fun login(loginBody: LoginPayload): ApiResult<Authorization>

    fun getUserStream(): ApiResult<LiveData<User>>

    suspend fun getUser(username: String): ApiResult<GetUserAPIModel>

    suspend fun followOrUnfollowUser(actionUser: User, targetUser: User)
}