package com.efecandonmez.subtracker.app.ui.subscriptions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.efecandonmez.subtracker.app.data.model.SubscriptionResponse
import com.efecandonmez.subtracker.app.data.network.SubtrackerApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class SubscriptionListUiState {
    object Loading : SubscriptionListUiState()
    data class Success(val subscriptions: List<SubscriptionResponse>) : SubscriptionListUiState()
    data class Error(val message: String) : SubscriptionListUiState()
}

class SubscriptionListViewModel(private val api: SubtrackerApi) : ViewModel() {

    private val _uiState = MutableStateFlow<SubscriptionListUiState>(SubscriptionListUiState.Loading)
    val uiState: StateFlow<SubscriptionListUiState> = _uiState

    fun loadSubscriptions() {
        viewModelScope.launch {
            _uiState.value = SubscriptionListUiState.Loading
            try {
                val subs = api.getSubscriptions()
                _uiState.value = SubscriptionListUiState.Success(subs)
            } catch (e: java.io.IOException) {
                _uiState.value = SubscriptionListUiState.Error("İnternet bağlantını kontrol et")
            } catch (e: retrofit2.HttpException) {
                _uiState.value = SubscriptionListUiState.Error("Abonelikler yüklenemedi, tekrar dene")
            } catch (e: Exception) {
                _uiState.value = SubscriptionListUiState.Error("Beklenmeyen bir hata oluştu")
            }
        }
    }

    fun deleteSubscription(id: String) {
        val currentState = _uiState.value
        if (currentState is SubscriptionListUiState.Success) {
            _uiState.value = SubscriptionListUiState.Success(
                currentState.subscriptions.filter { it.id != id }
            )
        }

        viewModelScope.launch {
            try {
                api.deleteSubscription(id)
            } catch (e: Exception) {
                loadSubscriptions()
            }
        }
    }
}