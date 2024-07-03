package com.submis.ourstory.injection

import com.submis.ourstory.dom.auth.usecase.AuthInteractor
import com.submis.ourstory.dom.auth.usecase.AuthUseCase
import com.submis.ourstory.dom.story.usecase.StoryInteractor
import com.submis.ourstory.dom.story.usecase.StoryUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun provideAuthUseCase(authInteractor: AuthInteractor): AuthUseCase

    @Binds
    @Singleton
    abstract fun provideStoryUseCase(storyInteractor: StoryInteractor): StoryUseCase
}