package com.submis.ourstory.injection

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.submis.ourstory.data.init.datastore.UserPreferences
import com.submis.ourstory.data.init.datastore.UserPreferencesImpl
import com.submis.ourstory.utils.Constanta.USER_PREFERENCES
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = USER_PREFERENCES)

@Module
@InstallIn(SingletonComponent::class)
abstract class DataStoreModule {

    @Binds
    @Singleton
    abstract fun provideUserPreferences(userPreferencesImpl: UserPreferencesImpl): UserPreferences

    companion object {
        @Provides
        @Singleton
        fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
            context.dataStore
    }
}