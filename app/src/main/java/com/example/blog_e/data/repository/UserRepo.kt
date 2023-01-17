package com.example.blog_e.data.repository

import androidx.lifecycle.LiveData
import com.example.blog_e.Config
import com.example.blog_e.data.model.*
import java.util.*

class UserRepo(private val backendS: BlogEAPI) : UserRepository {

    private val apiHandler: ApiHandler = ApiHandler(Config.tag(this.toString()))


    override suspend fun signUp(user: NewUserAPIModel): ApiResult<Authorization> {
        return apiHandler.handleApi { backendS.signUp(user) }
    }

    override suspend fun login(loginBody: LoginPayload): ApiResult<Authorization> {
        return apiHandler.handleApi { backendS.login(loginBody) }
    }


    override fun getUserStream(): ApiResult<LiveData<User>> {
        TODO("Not yet implemented")
    }

    override suspend fun getUser(username: String): ApiResult<GetUserAPIModel> {
        return apiHandler.handleApi { backendS.getUser(username) }
    }

    override suspend fun followOrUnfollowUser(actionUser: User, targetUser: User) {
        TODO("Not yet implemented")
    }

}