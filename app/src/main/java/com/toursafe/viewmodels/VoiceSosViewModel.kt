package com.toursafe.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.toursafe.util.SpeechRecognitionManager
import com.toursafe.util.SpeechState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class VoiceSosViewModel : ViewModel() {

    private var speechManager: SpeechRecognitionManager? = null

    private val _speechState = MutableStateFlow<SpeechState>(SpeechState.Idle)
    val speechState: StateFlow<SpeechState> = _speechState.asStateFlow()

    private val _spokenText = MutableStateFlow("")
    val spokenText: StateFlow<String> = _spokenText.asStateFlow()

    private val _selectedSpeechLanguage = MutableStateFlow("en")
    val selectedSpeechLanguage: StateFlow<String> = _selectedSpeechLanguage.asStateFlow()

    private val _includeGpsLocation = MutableStateFlow(true)
    val includeGpsLocation: StateFlow<Boolean> = _includeGpsLocation.asStateFlow()

    private val _messageSentSuccess = MutableStateFlow(false)
    val messageSentSuccess: StateFlow<Boolean> = _messageSentSuccess.asStateFlow()

    fun initSpeechManager(context: Context) {
        if (speechManager == null) {
            speechManager = SpeechRecognitionManager(context)
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
    }

    fun sendEmergencyMessage(onComplete: () -> Unit) {
        _messageSentSuccess.value = true
        onComplete()
    }

    override fun onCleared() {
        super.onCleared()
        speechManager?.stopListening()
    }
}
