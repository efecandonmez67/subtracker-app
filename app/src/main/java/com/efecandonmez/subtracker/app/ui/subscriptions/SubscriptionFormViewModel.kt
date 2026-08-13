package com.efecandonmez.subtracker.app.ui.subscriptions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.efecandonmez.subtracker.app.data.model.SubscriptionRequest
import com.efecandonmez.subtracker.app.data.network.SubtrackerApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class SubscriptionFormUiState {
    object Idle : SubscriptionFormUiState()
    object Loading : SubscriptionFormUiState()
    object Success : SubscriptionFormUiState()
    data class Error(val message: String) : SubscriptionFormUiState()
}

class SubscriptionFormViewModel(private val api: SubtrackerApi) : ViewModel() {

    private val _uiState = MutableStateFlow<SubscriptionFormUiState>(SubscriptionFormUiState.Idle)
    val uiState: StateFlow<SubscriptionFormUiState> = _uiState

    fun createSubscription(request: SubscriptionRequest) {
        viewModelScope.launch {
            _uiState.value = SubscriptionFormUiState.Loading
            try {
                api.createSubscription(request)
                _uiState.value = SubscriptionFormUiState.Success
            } catch (e: java.io.IOException) {
                _uiState.value = SubscriptionFormUiState.Error("İnternet bağlantını kontrol et")
            } catch (e: retrofit2.HttpException) {
                val message = when (e.code()) {
                    400 -> "Girdiğin bilgileri kontrol et"
                    else -> "Kaydedilemedi, tekrar dene"
                }
                _uiState.value = SubscriptionFormUiState.Error(message)
            } catch (e: Exception) {
                _uiState.value = SubscriptionFormUiState.Error("Beklenmeyen bir hata oluştu")
            }
        }
    }

    fun updateSubscription(id: String, request: SubscriptionRequest) {
        viewModelScope.launch {
            _uiState.value = SubscriptionFormUiState.Loading
            try {
                api.updateSubscription(id, request)
                _uiState.value = SubscriptionFormUiState.Success
            } catch (e: java.io.IOException) {
                _uiState.value = SubscriptionFormUiState.Error("İnternet bağlantını kontrol et")
            } catch (e: retrofit2.HttpException) {
                val message = when (e.code()) {
                    400 -> "Girdiğin bilgileri kontrol et"
                    404 -> "Bu abonelik bulunamadı"
                    else -> "Güncellenemedi, tekrar dene"
                }
                _uiState.value = SubscriptionFormUiState.Error(message)
            } catch (e: Exception) {
                _uiState.value = SubscriptionFormUiState.Error("Beklenmeyen bir hata oluştu")
            }
        }
    }
}