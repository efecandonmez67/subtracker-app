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
        val trimmedEmail = email.trim()
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val response = api.login(LoginRequest(trimmedEmail, password))
                tokenStore.saveToken(response.token)
                registerFcmToken()
                _uiState.value = AuthUiState.Success
            } catch (e: java.io.IOException) {
                _uiState.value = AuthUiState.Error("İnternet bağlantını kontrol et")
            } catch (e: retrofit2.HttpException) {
                val message = when (e.code()) {
                    401 -> "Email veya şifre hatalı"
                    400 -> "Geçerli bir email adresi girin"
                    else -> "Bir şeyler ters gitti, tekrar dene"
                }
                _uiState.value = AuthUiState.Error(message)
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error("Beklenmeyen bir hata oluştu")
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
        val trimmedEmail = email.trim()
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val response = api.register(RegisterRequest(trimmedEmail, password))
                tokenStore.saveToken(response.token)
                registerFcmToken()
                _uiState.value = AuthUiState.Success
            } catch (e: java.io.IOException) {
                _uiState.value = AuthUiState.Error("İnternet bağlantını kontrol et")
            } catch (e: retrofit2.HttpException) {
                val message = when (e.code()) {
                    409 -> "Bu email zaten kayıtlı"
                    400 -> "Girdiğin bilgileri kontrol et"
                    else -> "Bir şeyler ters gitti, tekrar dene"
                }
                _uiState.value = AuthUiState.Error(message)
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error("Beklenmeyen bir hata oluştu")
            }
        }
    }
}