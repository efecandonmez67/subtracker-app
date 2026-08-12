package com.efecandonmez.subtracker.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.efecandonmez.subtracker.app.data.local.TokenStore
import com.efecandonmez.subtracker.app.data.network.SubtrackerApi

class AuthViewModelFactory(
    private val api: SubtrackerApi,
    private val tokenStore: TokenStore
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return AuthViewModel(api, tokenStore) as T
    }
}