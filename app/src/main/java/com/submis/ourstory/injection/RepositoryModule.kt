package com.submis.ourstory.injection

import com.submis.ourstory.data.AuthRepositoryImpl
import com.submis.ourstory.data.StoryRepositoryImpl
import com.submis.ourstory.dom.auth.usecase.AuthRepository
import com.submis.ourstory.dom.story.usecase.StoryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun provideAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun provideStoryRepository(storyRepositoryImpl: StoryRepositoryImpl): StoryRepository
}