package com.submis.ourstory.dom.auth.usecase

import com.submis.ourstory.dom.Result
import com.submis.ourstory.dom.auth.model.Login
import com.submis.ourstory.dom.auth.model.Register
import kotlinx.coroutines.flow.Flow

interface AuthUseCase {
    fun invokeRegister(name: String, email: String, password: String): Flow<Result<Register>>

    fun invokeLogin(email: String, password: String): Flow<Result<Login>>

    fun getStatus(): Flow<Boolean>

    suspend fun wipeUserAuthorization()
}
