package com.toursafe.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.toursafe.data.mock.MockData
import com.toursafe.data.models.EmergencyContact
import com.toursafe.util.LocaleManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

class SettingsViewModel : ViewModel() {

    private val isDebugBuild: Boolean
        get() = try {
            val clazz = Class.forName("com.toursafe.BuildConfig")
            val field = clazz.getField("DEBUG")
            field.getBoolean(null)
        } catch (e: Exception) {
            false
        }

    private val _demoModeEnabled = MutableStateFlow(isDebugBuild)
    val demoModeEnabled: StateFlow<Boolean> = _demoModeEnabled.asStateFlow()

    private val _pushNotificationsEnabled = MutableStateFlow(true)
    val pushNotificationsEnabled: StateFlow<Boolean> = _pushNotificationsEnabled.asStateFlow()

    private val _priorityAlertsEnabled = MutableStateFlow(true)
    val priorityAlertsEnabled: StateFlow<Boolean> = _priorityAlertsEnabled.asStateFlow()

    private val _emergencyContacts = MutableStateFlow<List<EmergencyContact>>(emptyList())
    val emergencyContacts: StateFlow<List<EmergencyContact>> = _emergencyContacts.asStateFlow()

    private val _selectedLanguageCode = MutableStateFlow("en")
    val selectedLanguageCode: StateFlow<String> = _selectedLanguageCode.asStateFlow()

    private val _profileName = MutableStateFlow("")
    val profileName: StateFlow<String> = _profileName.asStateFlow()

    fun loadSettings(context: Context) {
        val prefs = context.getSharedPreferences("toursafe_settings", Context.MODE_PRIVATE)

        _demoModeEnabled.value = prefs.getBoolean("demo_mode_enabled", isDebugBuild)
        _pushNotificationsEnabled.value = prefs.getBoolean("push_notifications", true)
        _priorityAlertsEnabled.value = prefs.getBoolean("priority_alerts", true)
        _selectedLanguageCode.value = LocaleManager.getPersistedLanguage(context)
        _profileName.value = prefs.getString("profile_name", "") ?: ""

        // Load emergency contacts JSON
        val contactsJson = prefs.getString("emergency_contacts_json", null)
        if (!contactsJson.isNullOrBlank()) {
            try {
                val array = JSONArray(contactsJson)
                val loaded = mutableListOf<EmergencyContact>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    loaded.add(
                        EmergencyContact(
                            id = obj.optString("id", "c-$i"),
                            name = obj.optString("name"),
                            relationship = obj.optString("relationship"),
                            phoneNumber = obj.optString("phoneNumber"),
                            isPrimary = obj.optBoolean("isPrimary", false)
                        )
                    )
                }
                _emergencyContacts.value = loaded
            } catch (e: Exception) {
                _emergencyContacts.value = MockData.initialEmergencyContacts
            }
        } else {
            _emergencyContacts.value = MockData.initialEmergencyContacts
            saveEmergencyContacts(context, MockData.initialEmergencyContacts)
        }
    }

    fun toggleDemoMode(context: Context, enabled: Boolean) {
        _demoModeEnabled.value = enabled
        context.getSharedPreferences("toursafe_settings", Context.MODE_PRIVATE)
            .edit()
            .putBoolean("demo_mode_enabled", enabled)
            .apply()
    }

    fun togglePushNotifications(context: Context, enabled: Boolean) {
        _pushNotificationsEnabled.value = enabled
        context.getSharedPreferences("toursafe_settings", Context.MODE_PRIVATE)
            .edit()
            .putBoolean("push_notifications", enabled)
            .apply()
    }

    fun togglePriorityAlerts(context: Context, enabled: Boolean) {
        _priorityAlertsEnabled.value = enabled
        context.getSharedPreferences("toursafe_settings", Context.MODE_PRIVATE)
            .edit()
            .putBoolean("priority_alerts", enabled)
            .apply()
    }

    fun saveProfileName(context: Context, name: String) {
        _profileName.value = name
        context.getSharedPreferences("toursafe_settings", Context.MODE_PRIVATE)
            .edit()
            .putString("profile_name", name)
            .apply()
    }

    fun addEmergencyContact(context: Context, name: String, relationship: String, phone: String) {
        val newContact = EmergencyContact(
            id = "c-${System.currentTimeMillis()}",
            name = name,
            relationship = relationship,
            phoneNumber = phone,
            isPrimary = _emergencyContacts.value.isEmpty()
        )
        val updated = _emergencyContacts.value + newContact
        _emergencyContacts.value = updated
        saveEmergencyContacts(context, updated)
    }

    fun deleteEmergencyContact(context: Context, contactId: String) {
        val updated = _emergencyContacts.value.filter { it.id != contactId }
        _emergencyContacts.value = updated
        saveEmergencyContacts(context, updated)
    }

    private fun saveEmergencyContacts(context: Context, contacts: List<EmergencyContact>) {
        val array = JSONArray()
        contacts.forEach { c ->
            val obj = JSONObject()
            obj.put("id", c.id)
            obj.put("name", c.name)
            obj.put("relationship", c.relationship)
            obj.put("phoneNumber", c.phoneNumber)
            obj.put("isPrimary", c.isPrimary)
            array.put(obj)
        }
        context.getSharedPreferences("toursafe_settings", Context.MODE_PRIVATE)
            .edit()
            .putString("emergency_contacts_json", array.toString())
            .apply()
    }
}
