package com.toursafe.viewmodels

import androidx.lifecycle.ViewModel
import com.toursafe.data.mock.MockData
import com.toursafe.data.models.AlertSeverity
import com.toursafe.data.models.DisasterAlert
import com.toursafe.util.LocationUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AlertsViewModel : ViewModel() {

    private val _allAlerts = MutableStateFlow(MockData.alerts)
    private val _selectedFilter = MutableStateFlow<AlertSeverity?>(null)
    val selectedFilter: StateFlow<AlertSeverity?> = _selectedFilter.asStateFlow()

    private val _filteredAlerts = MutableStateFlow(MockData.alerts)
    val filteredAlerts: StateFlow<List<DisasterAlert>> = _filteredAlerts.asStateFlow()

    fun updateGpsLocation(userLat: Double, userLng: Double) {
        if (userLat == 0.0 && userLng == 0.0) return
        val updated = MockData.alerts.map { alert ->
            val dist = LocationUtils.calculateDistanceKm(userLat, userLng, alert.latitude, alert.longitude)
            alert.copy(distanceKm = dist)
        }.sortedBy { it.distanceKm }

        _allAlerts.value = updated
        filterBySeverity(_selectedFilter.value)
    }

    fun filterBySeverity(severity: AlertSeverity?) {
        _selectedFilter.value = severity
        if (severity == null) {
            _filteredAlerts.value = _allAlerts.value
        } else {
            _filteredAlerts.value = _allAlerts.value.filter { it.severity == severity }
        }
    }

    fun getAlertById(id: String): DisasterAlert? {
        return _allAlerts.value.find { it.id == id }
    }
}
