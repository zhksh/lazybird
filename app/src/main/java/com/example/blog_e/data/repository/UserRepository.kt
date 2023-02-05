package com.example.blog_e.data.repository

import com.example.blog_e.data.model.*

interface UserRepository {

    suspend fun signUp(user: NewUserAPIModel): ApiResult<Authorization>

    suspend fun login(loginBody: LoginPayload): ApiResult<Authorization>

    suspend fun getUser(username: String): ApiResult<GetUserAPIModel>

    suspend fun findUsers(search: String): ApiResult<FindUsersAPIModel>

    suspend fun updateUser(username: String, info: UpdateUserAPIModel): ApiResult<Unit>

    suspend fun followOrUnfollowUser(targetUser: String, isFollowing: Boolean): ApiResult<String>

    suspend fun createSelfDescription(completePayload: AutoCompleteOptions): ApiResult<LLMSelfDescription>

}