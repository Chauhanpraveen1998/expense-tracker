package com.praveen.expensetracker.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.praveen.expensetracker.data.preferences.UserPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userPreferencesManager: UserPreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            try {
                val isLoggedIn = userPreferencesManager.isLoggedIn.first()
                _uiState.update {
                    it.copy(
                        isCheckingAuth = false,
                        isLoggedIn = isLoggedIn
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isCheckingAuth = false,
                        isLoggedIn = false
                    )
                }
            }
        }
    }

    fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.LoginEmailChanged -> {
                _uiState.update { it.copy(loginEmail = event.email, loginEmailError = null) }
            }
            is AuthEvent.LoginPasswordChanged -> {
                _uiState.update { it.copy(loginPassword = event.password, loginPasswordError = null) }
            }
            AuthEvent.Login -> login()
            
            is AuthEvent.RegisterNameChanged -> {
                _uiState.update { it.copy(registerName = event.name, registerNameError = null) }
            }
            is AuthEvent.RegisterEmailChanged -> {
                _uiState.update { it.copy(registerEmail = event.email, registerEmailError = null) }
            }
            is AuthEvent.RegisterPasswordChanged -> {
                _uiState.update { it.copy(registerPassword = event.password, registerPasswordError = null) }
            }
            is AuthEvent.RegisterConfirmPasswordChanged -> {
                _uiState.update { it.copy(registerConfirmPassword = event.password, registerConfirmPasswordError = null) }
            }
            AuthEvent.Register -> register()
            
            AuthEvent.TogglePasswordVisibility -> {
                _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            }
            AuthEvent.ToggleConfirmPasswordVisibility -> {
                _uiState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
            }
            AuthEvent.ClearError -> {
                _uiState.update { it.copy(error = null) }
            }
            AuthEvent.Logout -> logout()
        }
    }

    private fun login() {
        val state = _uiState.value
        
        var hasError = false
        
        if (state.loginEmail.isBlank()) {
            _uiState.update { it.copy(loginEmailError = "Email is required") }
            hasError = true
        } else if (!isValidEmail(state.loginEmail)) {
            _uiState.update { it.copy(loginEmailError = "Invalid email format") }
            hasError = true
        }
        
        if (state.loginPassword.isBlank()) {
            _uiState.update { it.copy(loginPasswordError = "Password is required") }
            hasError = true
        } else if (state.loginPassword.length < 6) {
            _uiState.update { it.copy(loginPasswordError = "Password must be at least 6 characters") }
            hasError = true
        }
        
        if (hasError) return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val result = userPreferencesManager.loginUser(
                email = state.loginEmail.trim(),
                password = state.loginPassword
            )
            
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, isLoggedIn = true) }
                },
                onFailure = { e: Throwable ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            error = e.message ?: "Login failed"
                        ) 
                    }
                }
            )
        }
    }

    private fun register() {
        val state = _uiState.value
        
        var hasError = false
        
        if (state.registerName.isBlank()) {
            _uiState.update { it.copy(registerNameError = "Name is required") }
            hasError = true
        } else if (state.registerName.length < 2) {
            _uiState.update { it.copy(registerNameError = "Name must be at least 2 characters") }
            hasError = true
        }
        
        if (state.registerEmail.isBlank()) {
            _uiState.update { it.copy(registerEmailError = "Email is required") }
            hasError = true
        } else if (!isValidEmail(state.registerEmail)) {
            _uiState.update { it.copy(registerEmailError = "Invalid email format") }
            hasError = true
        }
        
        if (state.registerPassword.isBlank()) {
            _uiState.update { it.copy(registerPasswordError = "Password is required") }
            hasError = true
        } else if (state.registerPassword.length < 6) {
            _uiState.update { it.copy(registerPasswordError = "Password must be at least 6 characters") }
            hasError = true
        }
        
        if (state.registerConfirmPassword != state.registerPassword) {
            _uiState.update { it.copy(registerConfirmPasswordError = "Passwords do not match") }
            hasError = true
        }
        
        if (hasError) return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val result = userPreferencesManager.registerUser(
                name = state.registerName.trim(),
                email = state.registerEmail.trim(),
                password = state.registerPassword
            )
            
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, isLoggedIn = true) }
                },
                onFailure = { e: Throwable ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            error = e.message ?: "Registration failed"
                        ) 
                    }
                }
            )
        }
    }

    private fun logout() {
        viewModelScope.launch {
            userPreferencesManager.logout()
            _uiState.update { 
                AuthUiState(isCheckingAuth = false, isLoggedIn = false) 
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
