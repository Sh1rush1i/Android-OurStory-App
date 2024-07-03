package com.submis.ourstory.dom.auth.usecase

import com.submis.ourstory.dom.Result
import com.submis.ourstory.dom.auth.model.Login
import com.submis.ourstory.dom.auth.model.Register
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun registUser(name: String, email: String, password: String): Flow<Result<Register>>

    fun loginUser(email: String, password: String): Flow<Result<Login>>

    suspend fun deleteUser()

    fun getLoginStatus(): Flow<Boolean>
}