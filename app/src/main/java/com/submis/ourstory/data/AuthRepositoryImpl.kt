package com.submis.ourstory.data

import com.submis.ourstory.data.init.datastore.UserPreferences
import com.submis.ourstory.data.init.RemoteDataSource
import com.submis.ourstory.dom.Result
import com.submis.ourstory.dom.auth.usecase.AuthRepository
import com.submis.ourstory.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val dataSource: RemoteDataSource,
    private val userPrefs: UserPreferences,
) : AuthRepository {

    override fun registUser(name: String, email: String, password: String) = flow {
        emit(Result.Loading())
        try {
            val responseRemoteDataSource = dataSource.registUser(name, email, password)
            val output = responseRemoteDataSource.toRegisterDomain()

            emit(Result.Success(output))
        } catch (e: HttpException) {
            emit(Result.Error(e.parseErrorMessage()))
        } catch (e: Exception) {
            emit(Result.Error(e.message))
        }
    }.flowOn(Dispatchers.IO)

    override fun loginUser(email: String, password: String) = flow {
        emit(Result.Loading())
        try {
            val responseRemoteDataSource = dataSource.loginUser(email, password)
            val output = responseRemoteDataSource.toLoginDomain()
            userPrefs.run {
                saveToken(output.token.toString())
                setLoginStatus(true)
            }

            emit(Result.Success(output))
        } catch (e: HttpException) {
            emit(Result.Error(e.parseErrorMessage()))
        } catch (e: Exception) {
            emit(Result.Error(e.message))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun deleteUser() {
        userPrefs.run {
            deleteToken()
            setLoginStatus(false)
        }
    }

    override fun getLoginStatus() = userPrefs.getLoginStatus()
}