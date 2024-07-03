package com.submis.ourstory.utils

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object Constanta {
    const val USER_PREFERENCES = "user_preferences"
    val USER_TOKEN = stringPreferencesKey("user_token")
    val LOGIN_STATUS = booleanPreferencesKey("user_login")

    val emailPattern = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
}