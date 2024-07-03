package com.submis.ourstory.data.init.datastore

import kotlinx.coroutines.flow.Flow

interface UserPreferences {
    suspend fun saveToken(token: String)

    suspend fun deleteToken()

    fun getToken(): Flow<String>

    suspend fun setLoginStatus(isLogin: Boolean)

    fun getLoginStatus(): Flow<Boolean>
}