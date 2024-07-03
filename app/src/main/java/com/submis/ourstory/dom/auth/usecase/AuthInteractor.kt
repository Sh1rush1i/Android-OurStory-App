package com.submis.ourstory.dom.auth.usecase

import javax.inject.Inject

class AuthInteractor @Inject constructor(private val repository: AuthRepository) : AuthUseCase {

    override fun invokeRegister(name: String, email: String, password: String) =
        repository.registUser(name, email, password)

    override fun invokeLogin(email: String, password: String) =
        repository.loginUser(email, password)

    override fun getStatus() =
        repository.getLoginStatus()

    override suspend fun wipeUserAuthorization() =
        repository.deleteUser()
}