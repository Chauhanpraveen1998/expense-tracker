package com.praveen.expensetracker.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.praveen.expensetracker.data.preferences.UserPreferencesManager
import com.praveen.expensetracker.data.repository.AuthRepository
import com.praveen.expensetracker.data.sync.SyncManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val preferencesManager: UserPreferencesManager,
    private val syncManager: SyncManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            try {
                preferencesManager.userData.collect { prefs ->
                    _uiState.update {
                        it.copy(
                            notificationsEnabled = prefs.notificationsEnabled,
                            darkMode = when (prefs.darkMode) {
                                "light" -> DarkMode.LIGHT
                                "dark" -> DarkMode.DARK
                                else -> DarkMode.SYSTEM
                            }
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.ToggleNotifications -> toggleNotifications(event.enabled)
            is SettingsEvent.SetDarkMode -> setDarkMode(event.mode)
            SettingsEvent.SyncNow -> syncNow()
            SettingsEvent.Logout -> logout()
        }
    }

    private fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setNotificationsEnabled(enabled)
            _uiState.update { it.copy(notificationsEnabled = enabled) }
        }
    }

    private fun setDarkMode(mode: DarkMode) {
        viewModelScope.launch {
            val modeString = when (mode) {
                DarkMode.SYSTEM -> "system"
                DarkMode.LIGHT -> "light"
                DarkMode.DARK -> "dark"
            }
            preferencesManager.setDarkMode(modeString)
            _uiState.update { it.copy(darkMode = mode) }
        }
    }

    private fun syncNow() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val result = syncManager.syncAll()
                val now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, HH:mm"))
                preferencesManager.setLastSyncTime(now)
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        message = if (result.success) "Sync completed" else "Sync failed"
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        message = "Sync failed: ${e.message}"
                    )
                }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                authRepository.logout()
                _uiState.update { it.copy(isLoading = false, isLoggedOut = true) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        message = "Logout failed: ${e.message}"
                    )
                }
            }
        }
    }
}

data class SettingsUiState(
    val isLoading: Boolean = false,
    val isLoggedOut: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val darkMode: DarkMode = DarkMode.SYSTEM,
    val message: String? = null
)

enum class DarkMode {
    SYSTEM, LIGHT, DARK
}

sealed class SettingsEvent {
    data class ToggleNotifications(val enabled: Boolean) : SettingsEvent()
    data class SetDarkMode(val mode: DarkMode) : SettingsEvent()
    object SyncNow : SettingsEvent()
    object Logout : SettingsEvent()
}
