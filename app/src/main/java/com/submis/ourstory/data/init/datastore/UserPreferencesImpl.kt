package com.submis.ourstory.data.init.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.submis.ourstory.utils.Constanta
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesImpl @Inject constructor(private val dataStore: DataStore<Preferences>) :
    UserPreferences {

    override suspend fun saveToken(token: String) {
        dataStore.edit {
            it[Constanta.USER_TOKEN] = token
        }
    }

    override suspend fun deleteToken() {
        dataStore.edit {
            it.remove(Constanta.USER_TOKEN)
        }
    }

    override fun getToken() =
        dataStore.data.map {
            it[Constanta.USER_TOKEN] ?: ""
        }

    override suspend fun setLoginStatus(isLogin: Boolean) {
        dataStore.edit {
            it[Constanta.LOGIN_STATUS] = isLogin
        }
    }

    override fun getLoginStatus() =
        dataStore.data.map {
            it[Constanta.LOGIN_STATUS] ?: false
        }
}