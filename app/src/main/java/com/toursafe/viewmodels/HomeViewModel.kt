package com.toursafe.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewModelScope
import com.toursafe.data.models.DisasterAlert
import com.toursafe.data.models.SafetyLevel
import com.toursafe.data.models.UserSafetyStatus
import com.toursafe.data.repository.LoadStatus
import com.toursafe.data.repository.RepositoryProvider
import com.toursafe.data.repository.SafetyRepository
import com.toursafe.data.repository.items
import com.toursafe.data.repository.toStatus
import com.toursafe.util.LocationUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel(
    private val repository: SafetyRepository = RepositoryProvider.live
) : ViewModel() {

    private val _userStatus = MutableStateFlow(repository.getUserStatus())
    val userStatus: StateFlow<UserSafetyStatus> = _userStatus.asStateFlow()

    private val _alerts = MutableStateFlow<List<DisasterAlert>>(emptyList())
    val alerts: StateFlow<List<DisasterAlert>> = _alerts.asStateFlow()

    private val _alertsStatus = MutableStateFlow(LoadStatus.IDLE)
    val alertsStatus: StateFlow<LoadStatus> = _alertsStatus.asStateFlow()

    private var alertsJob: Job? = null

    fun refreshData(userLat: Double = 0.0, userLng: Double = 0.0, demoMode: Boolean = false, context: Context? = null) {
        alertsJob?.cancel()
        _alertsStatus.value = if (demoMode || userLat != 0.0 || userLng != 0.0) LoadStatus.LOADING else LoadStatus.IDLE
        alertsJob = viewModelScope.launch {
            val state = repository.getAlerts(userLat, userLng, demoMode)
            _alerts.value = state.items()
            _alertsStatus.value = state.toStatus()
        }

        if (userLat != 0.0 || userLng != 0.0) {
            val formattedCoords = "${"%.4f".format(userLat)}° N, ${"%.4f".format(userLng)}° E"
            val currentStatus = _userStatus.value

            if (context != null) {
                LocationUtils.reverseGeocodeLocationName(context, userLat, userLng) { cityName ->
                    _userStatus.value = currentStatus.copy(
                        locationName = cityName,
                        coordinates = formattedCoords
                    )
                }
            } else {
                _userStatus.value = currentStatus.copy(
                    coordinates = formattedCoords
                )
            }
        }
    }

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

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                HomeViewModel(RepositoryProvider.live)
            }
        }
    }
}
