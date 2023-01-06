package com.example.blog_e.di

import com.example.blog_e.data.repository.UserRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideUserRepo(): UserRepo {
        //TODO: create the retrofit instance in here
        return UserRepo()
    }
}