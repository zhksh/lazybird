package com.example.blog_e.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.blog_e.R
import com.example.blog_e.data.model.User

class SessionManager(context: Context) {
    private var prefs: SharedPreferences =
        context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    private lateinit var  user: User

    /**
     * Function to save auth token
     */
    fun saveSession(token: String, username: String) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.putString(USER_NAME, username)
        editor.apply()
    }

    fun getUsername(): String? {
        return prefs.getString(USER_NAME, null)
    }

    /**
     * Function to fetch auth token
     */
    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    fun resetSession() {
        val editor = prefs.edit()
        editor.remove(USER_TOKEN)
        editor.remove(USER_NAME)
        editor.apply()
    }

    companion object {
        const val USER_TOKEN = "user_token"
        const val USER_NAME = "user_name"
    }
}