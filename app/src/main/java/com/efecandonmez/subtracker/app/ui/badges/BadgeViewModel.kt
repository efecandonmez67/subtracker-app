package com.efecandonmez.subtracker.app.ui.badges

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.efecandonmez.subtracker.app.data.model.UserBadge
import com.efecandonmez.subtracker.app.data.network.SubtrackerApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BadgeViewModel(private val api: SubtrackerApi) : ViewModel() {

    private val _earnedBadges = MutableStateFlow<List<UserBadge>>(emptyList())
    val earnedBadges: StateFlow<List<UserBadge>> = _earnedBadges

    fun loadBadges() {
        viewModelScope.launch {
            try {
                _earnedBadges.value = api.getBadges()
            } catch (e: Exception) {
                _earnedBadges.value = emptyList()
            }
        }
    }
}