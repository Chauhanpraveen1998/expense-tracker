package com.praveen.expensetracker.data.remote

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

private val Context.tokenDataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_tokens")

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
    }
    
    val accessToken: Flow<String?> = context.tokenDataStore.data.map { preferences ->
        preferences[ACCESS_TOKEN_KEY]
    }
    
    val refreshToken: Flow<String?> = context.tokenDataStore.data.map { preferences ->
        preferences[REFRESH_TOKEN_KEY]
    }
    
    val isLoggedIn: Flow<Boolean> = context.tokenDataStore.data.map { preferences ->
        preferences[ACCESS_TOKEN_KEY] != null
    }
    
    val userId: Flow<String?> = context.tokenDataStore.data.map { preferences ->
        preferences[USER_ID_KEY]
    }
    
    val userName: Flow<String?> = context.tokenDataStore.data.map { preferences ->
        preferences[USER_NAME_KEY]
    }
    
    suspend fun saveTokens(accessToken: String, refreshToken: String?, userId: String, userName: String, email: String) {
        context.tokenDataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = accessToken
            refreshToken?.let { preferences[REFRESH_TOKEN_KEY] = it }
            preferences[USER_ID_KEY] = userId
            preferences[USER_NAME_KEY] = userName
            preferences[USER_EMAIL_KEY] = email
        }
    }
    
    suspend fun clearTokens() {
        context.tokenDataStore.edit { preferences ->
            preferences.clear()
        }
    }
    
    fun getAccessTokenSync(): String? {
        return runBlocking {
            accessToken.first()
        }
    }
    
    suspend fun getUserName(): String? {
        return userName.first()
    }
}
