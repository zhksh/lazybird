package com.example.blog_e.data.repository

import androidx.lifecycle.LiveData
import com.example.blog_e.data.model.Authorization
import com.example.blog_e.data.model.LoginPayload
import com.example.blog_e.data.model.User
import java.util.*

interface UserRepository {

    suspend fun signUp(user: User): ApiResult<Authorization>

    suspend fun login(loginBody: LoginPayload): ApiResult<Authorization>

    fun getUserStream(): ApiResult<LiveData<User>>

    suspend fun getUser(user: User): ApiResult<User>

    suspend fun getUser(userId: UUID)

    suspend fun followOrUnfollowUser(actionUser: User, targetUser: User)
}