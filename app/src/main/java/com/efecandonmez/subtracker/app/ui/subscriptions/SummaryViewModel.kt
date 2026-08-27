package com.efecandonmez.subtracker.app.ui.subscriptions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.efecandonmez.subtracker.app.data.model.SubscriptionSummary
import com.efecandonmez.subtracker.app.data.network.SubtrackerApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SummaryViewModel(private val api: SubtrackerApi) : ViewModel() {

    private val _summary = MutableStateFlow<SubscriptionSummary?>(null)
    val summary: StateFlow<SubscriptionSummary?> = _summary

    fun loadSummary() {
        viewModelScope.launch {
            try {
                _summary.value = api.getSummary()
            } catch (e: Exception) {
                _summary.value = null
            }
        }
    }
}