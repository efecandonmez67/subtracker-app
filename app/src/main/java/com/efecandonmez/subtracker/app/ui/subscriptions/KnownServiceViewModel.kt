package com.efecandonmez.subtracker.app.ui.subscriptions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.efecandonmez.subtracker.app.data.model.KnownService
import com.efecandonmez.subtracker.app.data.network.SubtrackerApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class KnownServiceViewModel(private val api: SubtrackerApi) : ViewModel() {

    private val _services = MutableStateFlow<List<KnownService>>(emptyList())
    val services: StateFlow<List<KnownService>> = _services

    init {
        viewModelScope.launch {
            try {
                _services.value = api.getKnownServices()
            } catch (e: Exception) {
                _services.value = emptyList()
            }
        }
    }
}