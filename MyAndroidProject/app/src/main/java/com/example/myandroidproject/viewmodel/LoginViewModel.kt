package com.example.myandroidproject.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myandroidproject.network.AuthRepository
import com.example.myandroidproject.utils.SessionManager
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoggedIn: Boolean = false,
    val rememberMe: Boolean = false,
)

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = AuthRepository()
    private val session = SessionManager.getInstance(application)

    var uiState by mutableStateOf(LoginUiState())
        private set

    fun onEmailChange(value: String) {
        uiState = uiState.copy(email = value, error = null)
    }

    fun onPasswordChange(value: String) {
        uiState = uiState.copy(password = value, error = null)
    }

    fun onRememberMeChange(value: Boolean) {
        uiState = uiState.copy(rememberMe = value)
    }

    fun login() {
        val state = uiState
        if (state.email.isBlank() || state.password.isBlank()) {
            uiState = state.copy(error = "Email and password are required")
            return
        }
        viewModelScope.launch {
            uiState = state.copy(isLoading = true, error = null)
            repo.login(state.email.trim(), state.password)
                .onSuccess { resp ->
                    session.saveSession(
                        accessToken = resp.accessToken,
                        refreshToken = resp.refreshToken,
                        userId = resp.user?.id,
                        email = resp.user?.email ?: state.email.trim(),
                        username = resp.user?.profile?.username,
                        rememberMe = state.rememberMe
                    )
                    uiState = uiState.copy(isLoading = false, isLoggedIn = true)
                }
                .onFailure { e ->
                    uiState = uiState.copy(
                        isLoading = false,
                        error = e.message ?: "Login failed"
                    )
                }
        }
    }

    fun consumeLoginSuccess() {
        uiState = uiState.copy(isLoggedIn = false)
    }
}