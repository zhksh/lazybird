package com.example.blog_e.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.blog_e.R
import com.example.blog_e.data.model.*
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.*

class UserRepo() : UserRepository {

    private var backendS: BlogEAPI

    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        backendS = retrofit.create(BlogEAPI::class.java)
    }

    override suspend fun signUp(user: User): Authorization {

        val userReq = NewUserAPIModel(
            user.username,
            R.drawable.among_us_0.toString(),
            user.password,
            user.username
        )
        val response: Response<Authorization> = try {
            backendS.signUp(userReq)
        } catch (e: IOException ) {
            Log.e(this.toString(), "Keine buffer")
            throw e
        } catch (e: HttpException) {
            Log.e(this.toString(), "Keine Verbindung bekommen")
            throw e
        }

        if (!response.isSuccessful) {
            Log.e(this.toString(), "Fehlermeldung" + response.code())

        }
        return response.body()!!
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