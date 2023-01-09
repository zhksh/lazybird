package com.example.blog_e.data.repository

import androidx.lifecycle.LiveData
import com.example.blog_e.data.model.Authorization
import com.example.blog_e.data.model.LoginPayload
import com.example.blog_e.data.model.NewUserAPIModel
import com.example.blog_e.data.model.User
import java.util.*

class UserRepo(private val backendS: BlogEAPI) : UserRepository {

    private val apiHandler: ApiHandler = ApiHandler(this.toString())


    override suspend fun signUp(user: User): ApiResult<Authorization> {
        val userReq = NewUserAPIModel(
            user.username,
            user.profilePicture.toString(),
            user.password,
            user.username
        )
        return apiHandler.handleApi { backendS.signUp(userReq) }
    }

    override suspend fun login(loginBody: LoginPayload): ApiResult<Authorization> {
        return apiHandler.handleApi { backendS.login(loginBody) }
    }


    override fun getUserStream(): ApiResult<LiveData<User>> {
        TODO("Not yet implemented")
    }

    override suspend fun getUser(user: User): ApiResult<User> {
        TODO("Muss noch gemacht werden")
    }

    override suspend fun getUser(userId: UUID) {
        TODO("Not yet implemented")
    }

    override suspend fun followOrUnfollowUser(actionUser: User, targetUser: User) {
        TODO("Not yet implemented")
    }

}