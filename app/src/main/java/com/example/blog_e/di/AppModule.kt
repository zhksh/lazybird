package com.example.blog_e.di

import android.content.Context
import com.example.blog_e.data.repository.ApiClient
import com.example.blog_e.data.repository.BlogRepo
import com.example.blog_e.data.repository.UserRepo
import com.example.blog_e.utils.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideUserRepo(@ApplicationContext context: Context): UserRepo {
        val apiClient = ApiClient()
        return UserRepo(apiClient.getApiService(context))
    }

    @Singleton
    @Provides
    fun provideBlogRepo(@ApplicationContext context: Context): BlogRepo {
        val apiClient = ApiClient()
        return BlogRepo(apiClient.getApiService(context))
    }


    @Provides
    fun provideSessionManager(@ApplicationContext context: Context): SessionManager {
        return SessionManager(context)
    }
}