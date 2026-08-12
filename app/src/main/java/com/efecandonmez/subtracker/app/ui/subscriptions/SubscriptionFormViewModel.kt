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
            } catch (e: Exception) {
                _uiState.value = SubscriptionFormUiState.Error(e.message ?: "Kaydedilemedi")
            }
        }
    }

    fun updateSubscription(id: String, request: SubscriptionRequest) {
        viewModelScope.launch {
            _uiState.value = SubscriptionFormUiState.Loading
            try {
                api.updateSubscription(id, request)
                _uiState.value = SubscriptionFormUiState.Success
            } catch (e: Exception) {
                _uiState.value = SubscriptionFormUiState.Error(e.message ?: "Güncellenemedi")
            }
        }
    }
}