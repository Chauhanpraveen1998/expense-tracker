package com.praveen.expensetracker.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

data class UserData(
    val isLoggedIn: Boolean = false,
    val userId: String? = null,
    val userName: String = "User",
    val email: String? = null,
    val monthlyBudget: Double = 50000.0
)

@Singleton
class UserPreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val USER_ID = stringPreferencesKey("user_id")
        val USER_NAME = stringPreferencesKey("user_name")
        val EMAIL = stringPreferencesKey("email")
        val PASSWORD_HASH = stringPreferencesKey("password_hash")
        val MONTHLY_BUDGET = stringPreferencesKey("monthly_budget")
        val IS_ONBOARDED = booleanPreferencesKey("is_onboarded")
    }

    val userData: Flow<UserData> = context.userDataStore.data.map { preferences ->
        UserData(
            isLoggedIn = preferences[PreferencesKeys.IS_LOGGED_IN] ?: false,
            userId = preferences[PreferencesKeys.USER_ID],
            userName = preferences[PreferencesKeys.USER_NAME] ?: "User",
            email = preferences[PreferencesKeys.EMAIL],
            monthlyBudget = preferences[PreferencesKeys.MONTHLY_BUDGET]?.toDoubleOrNull() ?: 50000.0
        )
    }

    val isLoggedIn: Flow<Boolean> = context.userDataStore.data.map { preferences ->
        preferences[PreferencesKeys.IS_LOGGED_IN] ?: false
    }

    suspend fun registerUser(
        name: String,
        email: String,
        password: String
    ): Result<Unit> {
        return try {
            context.userDataStore.edit { preferences ->
                preferences[PreferencesKeys.IS_LOGGED_IN] = true
                preferences[PreferencesKeys.USER_ID] = System.currentTimeMillis().toString()
                preferences[PreferencesKeys.USER_NAME] = name
                preferences[PreferencesKeys.EMAIL] = email
                preferences[PreferencesKeys.PASSWORD_HASH] = password.hashCode().toString()
                preferences[PreferencesKeys.IS_ONBOARDED] = true
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginUser(email: String, password: String): Result<Unit> {
        return try {
            val preferences = context.userDataStore.data.first()
            val savedEmail = preferences[PreferencesKeys.EMAIL]
            val savedPasswordHash = preferences[PreferencesKeys.PASSWORD_HASH]
            
            if (savedEmail == email && savedPasswordHash == password.hashCode().toString()) {
                context.userDataStore.edit { prefs ->
                    prefs[PreferencesKeys.IS_LOGGED_IN] = true
                }
                return Result.success(Unit)
            } else if (savedEmail == null) {
                context.userDataStore.edit { prefs ->
                    prefs[PreferencesKeys.IS_LOGGED_IN] = true
                    prefs[PreferencesKeys.USER_ID] = System.currentTimeMillis().toString()
                    prefs[PreferencesKeys.USER_NAME] = email.substringBefore("@")
                    prefs[PreferencesKeys.EMAIL] = email
                    prefs[PreferencesKeys.PASSWORD_HASH] = password.hashCode().toString()
                }
                return Result.success(Unit)
            } else {
                return Result.failure(Exception("Invalid email or password"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_LOGGED_IN] = false
        }
    }

    suspend fun updateUserName(name: String) {
        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_NAME] = name
        }
    }

    suspend fun updateMonthlyBudget(budget: Double) {
        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.MONTHLY_BUDGET] = budget.toString()
        }
    }

    suspend fun clearAllData() {
        context.userDataStore.edit { it.clear() }
    }
}
