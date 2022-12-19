package com.example.blog_e.data.repository

import androidx.lifecycle.LiveData
import com.example.blog_e.data.model.User
import java.util.*

interface UserRepository {

    suspend fun signUp(user: User)

    suspend fun login(user: User)

    fun getUserStream(): LiveData<User>

    suspend fun getUser(user: User): User

    suspend fun getUser(userId: UUID)

    suspend fun followOrUnfollowUser(actionUser: User, targetUser: User)
}