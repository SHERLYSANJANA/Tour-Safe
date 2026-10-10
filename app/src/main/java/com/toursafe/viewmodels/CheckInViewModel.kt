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

    private val _emergencyContacts = MutableStateFlow(MockData.initialEmergencyContacts)
    val emergencyContacts: StateFlow<List<EmergencyContact>> = _emergencyContacts.asStateFlow()

    private val _lastBroadcastMessage = MutableStateFlow<String?>(null)
    val lastBroadcastMessage: StateFlow<String?> = _lastBroadcastMessage.asStateFlow()

    fun selectStatus(level: SafetyLevel) {
        _selectedStatus.value = level
    }

    fun updateCustomNote(note: String) {
        _customNote.value = note
    }

    fun submitCheckIn(onComplete: () -> Unit) {
        val msg = when (_selectedStatus.value) {
            SafetyLevel.SAFE -> "Broadcasting 'I AM SAFE': ${_customNote.value.ifBlank { "All fine at my location." }}"
            SafetyLevel.MOVING_TO_SAFETY -> "Broadcasting 'MOVING TO SAFETY': ${_customNote.value.ifBlank { "In transit to shelter." }}"
            SafetyLevel.NEED_HELP -> "Broadcasting 'EMERGENCY HELP NEEDED': ${_customNote.value.ifBlank { "Please send assistance." }}"
            SafetyLevel.CRITICAL_WARNING -> "Broadcasting 'ALERT ACKNOWLEDGED'."
        }
        _lastBroadcastMessage.value = msg
        onComplete()
    }
}
