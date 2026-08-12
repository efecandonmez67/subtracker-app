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
            } catch (e: Exception) {
                _uiState.value = SubscriptionListUiState.Error(e.message ?: "Yüklenemedi")
            }
        }
    }
}