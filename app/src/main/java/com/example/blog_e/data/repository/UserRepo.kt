package com.example.blog_e.data.repository

import androidx.lifecycle.LiveData
import com.example.blog_e.data.model.User
import retrofit2.Retrofit
import java.util.*

class UserRepo(private val backendService: BackendService) : UserRepository {

    var backendS: BackendService

    init {
        val retrofit: Retrofit = Retrofit.Builder().baseUrl(BASE_URL).build()
        backendS = retrofit.create(BackendService::class.java)
    }

    override suspend fun signUp(user: User) {
        TODO("Not yet implemented")
    }

    override suspend fun login(user: User) {
        TODO("Not yet implemented")
    }

    override fun getUserStream(): LiveData<User> {
        TODO("Not yet implemented")
    }

    override suspend fun getUser(user: User): User {
        TODO("Muss noch gemacht werden")
    }

    override suspend fun getUser(userId: UUID) {
        TODO("Not yet implemented")
    }

    override suspend fun followOrUnfollowUser(actionUser: User, targetUser: User) {
        TODO("Not yet implemented")
    }
}