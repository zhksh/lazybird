package com.example.blog_e.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.blog_e.data.model.Authorization
import com.example.blog_e.data.model.LoginPayload
import com.example.blog_e.data.model.NewUserAPIModel
import com.example.blog_e.data.model.User
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.*

class UserRepo() : UserRepository {

    private var backendS: BlogEAPI

    private val TAG = this.toString()

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
            user.profilePicture.toString(),
            user.password,
            user.username
        )
        val response: Response<Authorization> = try {
            backendS.signUp(userReq)
        } catch (e: IOException) {
            Log.e(TAG, "IOExeption occured")
            throw e
        } catch (e: HttpException) {
            Log.e(TAG, "Could not fetch with http")
            throw e
        }
        if (!response.isSuccessful) {
            Log.e(TAG, "Response with code: " + response.code())
        }
        return response.body()!!
    }

    override suspend fun login(loginBody: LoginPayload): Authorization {
        println(loginBody)
        val response: Response<Authorization> = try {
            backendS.login(loginBody)
        } catch (e: IOException) {
            Log.e(TAG, "IOExeption occured")
            throw e
        } catch (e: HttpException) {
            Log.e(TAG, "Could not fetch with http")
            throw e
        }
        if (!response.isSuccessful) {
            Log.e(TAG, "Response with code: " + response.code())
        }
        println(response)
        Log.i(TAG,response.message())

        return response.body()!!
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