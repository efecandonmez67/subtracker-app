package com.efecandonmez.subtracker.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.efecandonmez.subtracker.app.data.local.TokenStore
import com.efecandonmez.subtracker.app.data.model.FcmTokenRequest
import com.efecandonmez.subtracker.app.data.model.LoginRequest
import com.efecandonmez.subtracker.app.data.model.RegisterRequest
import com.efecandonmez.subtracker.app.data.network.SubtrackerApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    object Success : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(
    private val api: SubtrackerApi,
    private val tokenStore: TokenStore
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState

    fun login(email: String, password: String) {
        android.util.Log.d("LoginDebug", "email='$email' length=${email.length}")
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val response = api.login(LoginRequest(email, password))
                tokenStore.saveToken(response.token)
                registerFcmToken()
                _uiState.value = AuthUiState.Success
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Giriş başarısız")
            }
        }
    }

    private suspend fun registerFcmToken() {
        try {
            val fcmToken = com.google.firebase.messaging.FirebaseMessaging.getInstance().token.await()
            api.updateFcmToken(FcmTokenRequest(fcmToken))
        } catch (e: Exception) {

        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val response = api.register(RegisterRequest(email, password))
                tokenStore.saveToken(response.token)
                registerFcmToken()
                _uiState.value = AuthUiState.Success
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Kayıt başarısız")
            }
        }
    }
}