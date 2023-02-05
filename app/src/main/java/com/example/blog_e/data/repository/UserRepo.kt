package com.example.blog_e.data.repository

import com.example.blog_e.Config
import com.example.blog_e.data.api.BlogEAPI
import com.example.blog_e.data.model.*

class UserRepo(private val backendS: BlogEAPI) : UserRepository {

    private val apiHandler: ApiHandler = ApiHandler(Config.tag(this.toString()))


    override suspend fun signUp(user: NewUserAPIModel): ApiResult<Authorization> {
        return apiHandler.handleApi { backendS.signUp(user) }
    }

    override suspend fun login(loginBody: LoginPayload): ApiResult<Authorization> {
        return apiHandler.handleApi { backendS.login(loginBody) }
    }

    override suspend fun getUser(username: String): ApiResult<GetUserAPIModel> {
        return apiHandler.handleApi { backendS.getUser(username) }
    }

    override suspend fun findUsers(search: String): ApiResult<FindUsersAPIModel> {
        return apiHandler.handleApi { backendS.findUsers(search) }
    }

    override suspend fun updateUser(username: String, info: UpdateUserAPIModel): ApiResult<Unit> {
        return apiHandler.handleApi { backendS.updateUser(username, info) }
    }

    override suspend fun createSelfDescription(completePayload: AutoCompleteOptions): ApiResult<LLMSelfDescription> {
        return apiHandler.handleApi { backendS.createSelfDescription(completePayload) }
    }

    override suspend fun followOrUnfollowUser(
        targetUser: String,
        isFollowing: Boolean
    ): ApiResult<String> {
        return if (isFollowing) {
            apiHandler.handleApi { backendS.unFollow(targetUser) }
        } else {
            apiHandler.handleApi { backendS.follow(targetUser) }
        }
    }

}