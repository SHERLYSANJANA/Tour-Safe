package com.toursafe.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.toursafe.data.mock.MockData
import com.toursafe.data.models.EmergencyContact
import com.toursafe.data.models.LanguageOption
import com.toursafe.util.LocaleManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel : ViewModel() {

    private val _languages = MutableStateFlow(MockData.languages)
    val languages: StateFlow<List<LanguageOption>> = _languages.asStateFlow()

    private val _pushNotificationsEnabled = MutableStateFlow(true)
    val pushNotificationsEnabled: StateFlow<Boolean> = _pushNotificationsEnabled.asStateFlow()

    private val _disasterAlertsEnabled = MutableStateFlow(true)
    val disasterAlertsEnabled: StateFlow<Boolean> = _disasterAlertsEnabled.asStateFlow()

    private val _locationPermissionMock = MutableStateFlow(true)
    val locationPermissionMock: StateFlow<Boolean> = _locationPermissionMock.asStateFlow()

    private val _emergencyContacts = MutableStateFlow(MockData.initialEmergencyContacts)
    val emergencyContacts: StateFlow<List<EmergencyContact>> = _emergencyContacts.asStateFlow()

    private val _notificationsHistory = MutableStateFlow(MockData.initialNotifications)
    val notificationsHistory: StateFlow<List<String>> = _notificationsHistory.asStateFlow()

    fun selectLanguage(code: String, context: Context? = null) {
        _languages.value = _languages.value.map {
            it.copy(isSelected = (it.code == code))
        }
        if (context != null) {
            LocaleManager.setLocale(context, code)
        }
    }

    fun togglePushNotifications(enabled: Boolean) {
        _pushNotificationsEnabled.value = enabled
    }

    fun toggleDisasterAlerts(enabled: Boolean) {
        _disasterAlertsEnabled.value = enabled
    }

    fun toggleLocationPermissionMock(enabled: Boolean) {
        _locationPermissionMock.value = enabled
    }

    fun addContact(name: String, relation: String, phone: String) {
        val contact = EmergencyContact(
            id = "c-${System.currentTimeMillis()}",
            name = name,
            relationship = relation,
            phoneNumber = phone,
            isPrimary = _emergencyContacts.value.isEmpty()
        )
        _emergencyContacts.value = _emergencyContacts.value + contact
    }

    fun removeContact(id: String) {
        _emergencyContacts.value = _emergencyContacts.value.filter { it.id != id }
    }
}
