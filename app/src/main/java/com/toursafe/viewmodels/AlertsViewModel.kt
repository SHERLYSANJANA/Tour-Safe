package com.toursafe.viewmodels

import androidx.lifecycle.ViewModel
import com.toursafe.data.mock.MockData
import com.toursafe.data.models.AlertSeverity
import com.toursafe.data.models.DisasterAlert
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AlertsViewModel : ViewModel() {

    private val _allAlerts = MutableStateFlow(MockData.alerts)
    private val _selectedFilter = MutableStateFlow<AlertSeverity?>(null)
    val selectedFilter: StateFlow<AlertSeverity?> = _selectedFilter.asStateFlow()

    private val _filteredAlerts = MutableStateFlow(MockData.alerts)
    val filteredAlerts: StateFlow<List<DisasterAlert>> = _filteredAlerts.asStateFlow()

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
