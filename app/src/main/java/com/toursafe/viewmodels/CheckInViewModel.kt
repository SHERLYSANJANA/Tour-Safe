package com.toursafe.viewmodels

import androidx.lifecycle.ViewModel
import com.toursafe.data.mock.MockData
import com.toursafe.data.models.EmergencyContact
import com.toursafe.data.models.SafetyLevel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CheckInViewModel : ViewModel() {

    private val _selectedStatus = MutableStateFlow(SafetyLevel.SAFE)
    val selectedStatus: StateFlow<SafetyLevel> = _selectedStatus.asStateFlow()

    private val _customNote = MutableStateFlow("")
    val customNote: StateFlow<String> = _customNote.asStateFlow()

    private val _emergencyContacts = MutableStateFlow<List<EmergencyContact>>(emptyList())
    val emergencyContacts: StateFlow<List<EmergencyContact>> = _emergencyContacts.asStateFlow()

    private val _lastUpdatedMessage = MutableStateFlow<String?>(null)
    val lastBroadcastMessage: StateFlow<String?> = _lastUpdatedMessage.asStateFlow()

    fun selectStatus(level: SafetyLevel) {
        _selectedStatus.value = level
    }

    fun updateCustomNote(note: String) {
        _customNote.value = note
    }

    fun submitCheckIn(onComplete: () -> Unit) {
        val msg = when (_selectedStatus.value) {
            SafetyLevel.SAFE -> "Status set to 'I AM SAFE': ${_customNote.value.ifBlank { "All fine at my location." }}"
            SafetyLevel.MOVING_TO_SAFETY -> "Status set to 'MOVING TO SAFETY': ${_customNote.value.ifBlank { "In transit to shelter." }}"
            SafetyLevel.NEED_HELP -> "Status set to 'EMERGENCY HELP NEEDED': ${_customNote.value.ifBlank { "Assistance requested." }}"
            SafetyLevel.CRITICAL_WARNING -> "Status set to 'ALERT ACKNOWLEDGED'."
        }
        _lastUpdatedMessage.value = msg
        onComplete()
    }
}
