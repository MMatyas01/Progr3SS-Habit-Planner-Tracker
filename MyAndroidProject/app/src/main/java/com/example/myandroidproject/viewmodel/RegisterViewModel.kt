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

data class RegisterUiState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRegistered: Boolean = false
)

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = AuthRepository()
    private val session = SessionManager.getInstance(application)

    var uiState by mutableStateOf(RegisterUiState())
        private set

    fun onUsernameChange(v: String) { uiState = uiState.copy(username = v, error = null) }
    fun onEmailChange(v: String)    { uiState = uiState.copy(email = v, error = null) }
    fun onPasswordChange(v: String) { uiState = uiState.copy(password = v, error = null) }
    fun onConfirmChange(v: String)  { uiState = uiState.copy(confirmPassword = v, error = null) }

    fun register() {
        val s = uiState
        when {
            s.username.isBlank()   -> { uiState = s.copy(error = "Username is required"); return }
            s.email.isBlank()      -> { uiState = s.copy(error = "Email is required");    return }
            s.password.length < 6  -> { uiState = s.copy(error = "Password must be at least 6 characters"); return }
            s.password != s.confirmPassword -> {
                uiState = s.copy(error = "Passwords do not match"); return
            }
        }
        viewModelScope.launch {
            uiState = s.copy(isLoading = true, error = null)
            repo.register(s.username.trim(), s.email.trim(), s.password)
                .onSuccess {
                    // NEM mentünk tokent, csak jelzünk a sikerről!
                    uiState = uiState.copy(isLoading = false, isRegistered = true)
                }
                .onFailure { e ->
                    uiState = uiState.copy(
                        isLoading = false,
                        error = e.message ?: "Registration failed"
                    )
                }
        }
    }

    fun consumeRegisterSuccess() {
        uiState = uiState.copy(isRegistered = false)
    }
}