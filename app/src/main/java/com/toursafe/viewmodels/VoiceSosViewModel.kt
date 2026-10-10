package com.toursafe.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.toursafe.data.models.EmergencyContact
import com.toursafe.util.LocationState
import com.toursafe.util.RealLocationManager
import com.toursafe.util.SpeechRecognitionManager
import com.toursafe.util.SpeechState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class SosHandOffState {
    object NotPrepared : SosHandOffState()
    data class Prepared(
        val message: String,
        val smsNumbers: String,
        val mapsLink: String?
    ) : SosHandOffState()
    data class HandedOff(val intentType: String) : SosHandOffState()
}

class VoiceSosViewModel : ViewModel() {

    private var speechManager: SpeechRecognitionManager? = null
    private var locationManager: RealLocationManager? = null

    private val _speechState = MutableStateFlow<SpeechState>(SpeechState.Idle)
    val speechState: StateFlow<SpeechState> = _speechState.asStateFlow()

    private val _spokenText = MutableStateFlow("")
    val spokenText: StateFlow<String> = _spokenText.asStateFlow()

    private val _selectedSpeechLanguage = MutableStateFlow("en")
    val selectedSpeechLanguage: StateFlow<String> = _selectedSpeechLanguage.asStateFlow()

    private val _includeGpsLocation = MutableStateFlow(true)
    val includeGpsLocation: StateFlow<Boolean> = _includeGpsLocation.asStateFlow()

    private val _locationState = MutableStateFlow<LocationState>(LocationState.Idle)
    val locationState: StateFlow<LocationState> = _locationState.asStateFlow()

    private val _handOffState = MutableStateFlow<SosHandOffState>(SosHandOffState.NotPrepared)
    val handOffState: StateFlow<SosHandOffState> = _handOffState.asStateFlow()

    fun initManagers(context: Context) {
        if (speechManager == null) {
            speechManager = SpeechRecognitionManager(context)
        }
        if (locationManager == null) {
            locationManager = RealLocationManager(context)
        }
        fetchRealGpsLocation()
    }

    fun fetchRealGpsLocation() {
        val lm = locationManager ?: return
        if (lm.hasLocationPermission()) {
            _locationState.value = LocationState.Loading
            lm.fetchCurrentLocation { state ->
                _locationState.value = state
            }
        }
    }

    fun setSpeechLanguage(langCode: String) {
        _selectedSpeechLanguage.value = langCode
    }

    fun startListening() {
        _speechState.value = SpeechState.Listening
        speechManager?.startListening(_selectedSpeechLanguage.value) { state ->
            _speechState.value = state
            if (state is SpeechState.Success) {
                _spokenText.value = state.recognizedText
            }
        }
    }

    fun stopListening() {
        speechManager?.stopListening()
        _speechState.value = SpeechState.Idle
    }

    fun updateSpokenText(text: String) {
        _spokenText.value = text
    }

    fun toggleIncludeGpsLocation(enabled: Boolean) {
        _includeGpsLocation.value = enabled
        if (enabled && _locationState.value !is LocationState.Success) {
            fetchRealGpsLocation()
        }
    }

    fun prepareSosHandOff(contacts: List<EmergencyContact>) {
        val body = _spokenText.value.ifBlank { "EMERGENCY SOS: Assistance needed." }
        val locSuccess = _locationState.value as? LocationState.Success
        val mapsLink = if (_includeGpsLocation.value && locSuccess != null) {
            locSuccess.location.toGoogleMapsLink()
        } else null

        val numbers = contacts.map { it.phoneNumber.replace(Regex("[^0-9+]"), "") }.filter { it.isNotBlank() }.joinToString(",")
        val formattedMsg = if (mapsLink != null) "$body\n\nMap: $mapsLink" else body

        _handOffState.value = SosHandOffState.Prepared(
            message = formattedMsg,
            smsNumbers = numbers, // empty when the user has no saved contacts; they pick a recipient in their SMS app
            mapsLink = mapsLink
        )
    }

    fun markHandedOff(intentType: String) {
        _handOffState.value = SosHandOffState.HandedOff(intentType)
    }

    fun resetHandOffState() {
        _handOffState.value = SosHandOffState.NotPrepared
    }

    override fun onCleared() {
        super.onCleared()
        speechManager?.stopListening()
    }
}
