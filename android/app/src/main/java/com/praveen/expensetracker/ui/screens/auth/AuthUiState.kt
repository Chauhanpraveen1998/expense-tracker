package com.praveen.expensetracker.ui.screens.auth

data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val isCheckingAuth: Boolean = true,
    val error: String? = null,
    
    val loginEmail: String = "",
    val loginPassword: String = "",
    val loginEmailError: String? = null,
    val loginPasswordError: String? = null,
    
    val registerName: String = "",
    val registerEmail: String = "",
    val registerPassword: String = "",
    val registerConfirmPassword: String = "",
    val registerNameError: String? = null,
    val registerEmailError: String? = null,
    val registerPasswordError: String? = null,
    val registerConfirmPasswordError: String? = null,
    
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false
)

sealed class AuthEvent {
    data class LoginEmailChanged(val email: String) : AuthEvent()
    data class LoginPasswordChanged(val password: String) : AuthEvent()
    object Login : AuthEvent()
    
    data class RegisterNameChanged(val name: String) : AuthEvent()
    data class RegisterEmailChanged(val email: String) : AuthEvent()
    data class RegisterPasswordChanged(val password: String) : AuthEvent()
    data class RegisterConfirmPasswordChanged(val password: String) : AuthEvent()
    object Register : AuthEvent()
    
    object TogglePasswordVisibility : AuthEvent()
    object ToggleConfirmPasswordVisibility : AuthEvent()
    object ClearError : AuthEvent()
    object Logout : AuthEvent()
}
