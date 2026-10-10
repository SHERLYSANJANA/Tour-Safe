package com.toursafe.viewmodels

import androidx.lifecycle.ViewModel
import com.toursafe.data.mock.MockData
import com.toursafe.data.models.DisasterAlert
import com.toursafe.data.models.SafetyLevel
import com.toursafe.data.models.UserSafetyStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {

    private val _userStatus = MutableStateFlow(UserSafetyStatus())
    val userStatus: StateFlow<UserSafetyStatus> = _userStatus.asStateFlow()

    private val _alerts = MutableStateFlow(MockData.alerts)
    val alerts: StateFlow<List<DisasterAlert>> = _alerts.asStateFlow()

    fun updateSafetyLevel(newLevel: SafetyLevel, customMessage: String? = null) {
        val current = _userStatus.value
        val msg = customMessage ?: when (newLevel) {
            SafetyLevel.SAFE -> "I am safe and in a secure location."
            SafetyLevel.MOVING_TO_SAFETY -> "Currently evacuating to designated safe ground."
            SafetyLevel.NEED_HELP -> "Emergency assistance needed at current GPS coordinates."
            SafetyLevel.CRITICAL_WARNING -> "In proximity to active disaster hazard zone."
        }
        _userStatus.value = current.copy(
            level = newLevel,
            lastUpdatedText = "Just now",
            statusMessage = msg
        )
    }
}
