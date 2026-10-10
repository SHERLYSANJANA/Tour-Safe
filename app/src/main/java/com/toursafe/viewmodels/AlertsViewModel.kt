package com.toursafe.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toursafe.data.mock.MockData
import com.toursafe.data.models.AlertSeverity
import com.toursafe.data.models.DisasterAlert
import com.toursafe.data.repository.LoadStatus
import com.toursafe.data.repository.RepositoryProvider
import com.toursafe.data.repository.SafetyRepository
import com.toursafe.data.repository.items
import com.toursafe.data.repository.toStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AlertsViewModel(
    private val repository: SafetyRepository = RepositoryProvider.live
) : ViewModel() {

    private val _allAlerts = MutableStateFlow<List<DisasterAlert>>(emptyList())
    val allAlerts: StateFlow<List<DisasterAlert>> = _allAlerts.asStateFlow()
    private val _selectedFilter = MutableStateFlow<AlertSeverity?>(null)
    val selectedFilter: StateFlow<AlertSeverity?> = _selectedFilter.asStateFlow()

    private val _filteredAlerts = MutableStateFlow<List<DisasterAlert>>(emptyList())
    val filteredAlerts: StateFlow<List<DisasterAlert>> = _filteredAlerts.asStateFlow()

    private val _status = MutableStateFlow(LoadStatus.IDLE)
    val status: StateFlow<LoadStatus> = _status.asStateFlow()

    private var refreshJob: Job? = null

    fun refreshData(userLat: Double = 0.0, userLng: Double = 0.0, demoMode: Boolean = false) {
        refreshJob?.cancel()
        _status.value = if (demoMode || userLat != 0.0 || userLng != 0.0) LoadStatus.LOADING else LoadStatus.IDLE
        refreshJob = viewModelScope.launch {
            val state = repository.getAlerts(userLat, userLng, demoMode)
            _allAlerts.value = state.items()
            _status.value = state.toStatus()
            filterBySeverity(_selectedFilter.value)
        }
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
