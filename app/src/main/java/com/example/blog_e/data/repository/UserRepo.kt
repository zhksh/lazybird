package com.example.blog_e.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.blog_e.R
import com.example.blog_e.data.model.CreateUser
import com.example.blog_e.data.model.User
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.*

class UserRepo() : UserRepository {

    private var backendS: BackendService

    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        backendS = retrofit.create(BackendService::class.java)
    }

    override suspend fun signUp(user: User) {
        val userReq = CreateUser(
            user.username,
            R.drawable.among_us_0.toString(),
            user.password,
            user.username
        )
        val response = try {
            backendS.signUp(userReq)
        } catch (e: IOException ) {
            Log.e(this.toString(), "Keine buffer")
        } catch (e: HttpException) {
            Log.e(this.toString(), "Keine Verbindung bekommen")
        }
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