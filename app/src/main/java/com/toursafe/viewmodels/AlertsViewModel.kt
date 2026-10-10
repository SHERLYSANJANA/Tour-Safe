package com.toursafe.viewmodels

import androidx.lifecycle.ViewModel
import com.toursafe.data.mock.MockData
import com.toursafe.data.models.AlertSeverity
import com.toursafe.data.models.DisasterAlert
import com.toursafe.data.repository.DataSourceState
import com.toursafe.data.repository.MockSafetyRepository
import com.toursafe.data.repository.SafetyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AlertsViewModel(
    private val repository: SafetyRepository = MockSafetyRepository()
) : ViewModel() {

    private val _allAlerts = MutableStateFlow<List<DisasterAlert>>(emptyList())
    private val _selectedFilter = MutableStateFlow<AlertSeverity?>(null)
    val selectedFilter: StateFlow<AlertSeverity?> = _selectedFilter.asStateFlow()

    private val _filteredAlerts = MutableStateFlow<List<DisasterAlert>>(emptyList())
    val filteredAlerts: StateFlow<List<DisasterAlert>> = _filteredAlerts.asStateFlow()

    fun refreshData(userLat: Double = 0.0, userLng: Double = 0.0, demoMode: Boolean = false) {
        val state = repository.getAlerts(userLat, userLng, demoMode)
        _allAlerts.value = when (state) {
            is DataSourceState.Success -> state.data
            is DataSourceState.NoVerifiedSource -> emptyList()
        }
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
        return _allAlerts.value.find { it.id == id } ?: MockData.demoAlerts.find { it.id == id }
    }
}
