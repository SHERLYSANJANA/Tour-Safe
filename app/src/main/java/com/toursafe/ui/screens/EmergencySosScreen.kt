package com.toursafe.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.toursafe.R
import com.toursafe.ui.components.TourSafeTopBar
import com.toursafe.ui.theme.*
import com.toursafe.util.LocationState
import com.toursafe.util.SpeechState
import com.toursafe.viewmodels.SettingsViewModel
import com.toursafe.viewmodels.SosHandOffState
import com.toursafe.viewmodels.VoiceSosViewModel

@Composable
fun EmergencySosScreen(
    voiceViewModel: VoiceSosViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel(),
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        voiceViewModel.initManagers(context)
    }

    val speechState by voiceViewModel.speechState.collectAsState()
    val spokenText by voiceViewModel.spokenText.collectAsState()
    val selectedLang by voiceViewModel.selectedSpeechLanguage.collectAsState()
    val includeGps by voiceViewModel.includeGpsLocation.collectAsState()
    val locationState by voiceViewModel.locationState.collectAsState()
    val handOffState by voiceViewModel.handOffState.collectAsState()
    val emergencyContacts by settingsViewModel.emergencyContacts.collectAsState()

    var activeTab by remember { mutableIntStateOf(0) } // 0: Voice SOS, 1: Tap SOS
    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasMicPermission = isGranted
        if (isGranted) {
            voiceViewModel.startListening()
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fine = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarse = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fine || coarse) {
            voiceViewModel.fetchRealGpsLocation()
        }
    }

    var showConfirmDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TourSafeTopBar(
                title = stringResource(R.string.emergency_sos_title),
                showBackButton = true,
                onBackClick = onBackClick
            )
        },
        containerColor = BackgroundNavy
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Mode Tab Row (Voice SOS vs Tap SOS)
            TabRow(
                selectedTabIndex = activeTab,
                containerColor = SurfaceNavy,
                contentColor = AccentCyan,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    text = {
                        Text(
                            text = stringResource(R.string.voice_sos_tab),
                            style = MaterialTheme.typography.titleMedium,
                            color = if (activeTab == 0) AccentCyan else TextSecondary
                        )
                    }
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    text = {
                        Text(
                            text = stringResource(R.string.type_sos_tab),
                            style = MaterialTheme.typography.titleMedium,
                            color = if (activeTab == 1) AccentCyan else TextSecondary
                        )
                    }
                )
            }

            if (activeTab == 0) {
                // FEATURE 2: VOICE SOS MODE
                Text(
                    text = stringResource(R.string.voice_sos_instruction),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                // Speech Spoken Language Override Selector
                LanguageDropdownSelector(
                    selectedCode = selectedLang,
                    onSelectLanguage = { voiceViewModel.setSpeechLanguage(it) }
                )

                // Microphone Permission Notice or Pulse Button
                if (!hasMicPermission) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceNavy, RoundedCornerShape(16.dp))
                            .border(1.dp, WarningAmber, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = stringResource(R.string.mic_permission_title),
                                style = MaterialTheme.typography.titleMedium,
                                color = WarningAmber
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = stringResource(R.string.mic_permission_desc),
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO) },
                                colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.grant_mic_permission),
                                    color = Color.Black,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }
                } else {
                    // Pulsing Microphone Button
                    val isListening = speechState is SpeechState.Listening
                    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                    val pulseScale by infiniteTransition.animateFloat(
                        initialValue = 1.0f,
                        targetValue = if (isListening) 1.25f else 1.0f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(800, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "scale"
                    )

                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(if (isListening) AlertRedBg else SurfaceNavy)
                            .border(3.dp, if (isListening) AlertRed else AccentCyan, CircleShape)
                            .clickable {
                                if (isListening) {
                                    voiceViewModel.stopListening()
                                } else {
                                    voiceViewModel.startListening()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = if (isListening) Icons.Default.Mic else Icons.Default.MicOff,
                                contentDescription = null,
                                tint = if (isListening) AlertRed else AccentCyan,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isListening) stringResource(R.string.stop_listening) else stringResource(R.string.start_voice_sos),
                                style = MaterialTheme.typography.labelSmall,
                                color = TextPrimary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Status Indicator & Retry Button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = when (speechState) {
                                is SpeechState.Listening -> stringResource(R.string.status_listening)
                                is SpeechState.Processing -> stringResource(R.string.status_processing)
                                is SpeechState.Error -> (speechState as SpeechState.Error).errorMessage
                                else -> stringResource(R.string.status_idle)
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = if (speechState is SpeechState.Error) AlertRed else TextSecondary,
                            textAlign = TextAlign.Center
                        )

                        if ((speechState is SpeechState.Error) || (speechState is SpeechState.Success)) {
                            IconButton(onClick = { voiceViewModel.startListening() }) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Retry",
                                    tint = AccentCyan
                                )
                            }
                        }
                    }
                }

                // Audio Processing Explanation
                Text(
                    text = stringResource(R.string.speech_processing_note),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )

                // Editable Transcript Field
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.review_emergency_msg),
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = spokenText,
                        onValueChange = { voiceViewModel.updateSpokenText(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(stringResource(R.string.status_idle), color = TextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SurfaceNavy,
                            unfocusedContainerColor = SurfaceNavy,
                            focusedBorderColor = AccentCyan,
                            unfocusedBorderColor = SurfaceVariantNavy,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(14.dp),
                        maxLines = 4
                    )
                }

            } else {
                // TAP / TYPED SOS MODE
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.disaster_sos_assistance),
                        style = MaterialTheme.typography.labelSmall,
                        color = AlertRed
                    )
                    Text(
                        text = stringResource(R.string.sos_explanation),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .clip(CircleShape)
                            .background(AlertRedBg)
                            .border(4.dp, AlertRed, CircleShape)
                            .clickable { showConfirmDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "SOS",
                                tint = AlertRed,
                                modifier = Modifier.size(54.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = stringResource(R.string.tap_for_sos),
                                style = MaterialTheme.typography.headlineMedium,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }

            // Shared GPS Coordinates Review Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceNavy, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.shared_gps_coords),
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary
                        )
                        Switch(
                            checked = includeGps,
                            onCheckedChange = { voiceViewModel.toggleIncludeGpsLocation(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = SafetyGreen)
                        )
                    }

                    if (includeGps) {
                        Spacer(modifier = Modifier.height(6.dp))
                        when (locationState) {
                            is LocationState.Success -> {
                                val loc = (locationState as LocationState.Success).location
                                Text(
                                    text = "Lat: ${"%.4f".format(loc.latitude)}, Lng: ${"%.4f".format(loc.longitude)}",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = AccentCyan
                                )
                                Text(
                                    text = stringResource(R.string.location_accuracy, loc.accuracyMeters),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = stringResource(R.string.google_maps_link_label, loc.toGoogleMapsLink()),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SafetyGreen
                                )
                            }
                            is LocationState.Loading -> {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = AccentCyan, strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = stringResource(R.string.gps_acquiring),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )
                                }
                            }
                            is LocationState.PermissionDenied -> {
                                Button(
                                    onClick = {
                                        locationPermissionLauncher.launch(
                                            arrayOf(
                                                Manifest.permission.ACCESS_FINE_LOCATION,
                                                Manifest.permission.ACCESS_COARSE_LOCATION
                                            )
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(stringResource(R.string.grant_mic_permission), color = Color.Black, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                            else -> {
                                Text(
                                    text = "GPS Location unavailable. Message will be prepared without coordinates.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stringResource(R.string.location_permission_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }

            // Prepare & Hand-off Emergency Message Button
            Button(
                onClick = {
                    voiceViewModel.prepareSosHandOff(emergencyContacts)
                    showConfirmDialog = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AlertRed),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.send_emergency_message),
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        if (showConfirmDialog) {
            val preparedState = handOffState as? SosHandOffState.Prepared

            AlertDialog(
                onDismissRequest = {
                    showConfirmDialog = false
                    voiceViewModel.resetHandOffState()
                },
                containerColor = SurfaceNavy,
                title = { Text(stringResource(R.string.confirm_send_msg_title), color = AlertRed) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(stringResource(R.string.confirm_send_msg_desc), color = TextPrimary)
                        if (preparedState != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(SurfaceVariantNavy, RoundedCornerShape(10.dp))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = preparedState.message,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                preparedState?.let { prep ->
                                    val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
                                        data = Uri.parse("smsto:${prep.smsNumbers}")
                                        putExtra("sms_body", prep.message)
                                        putExtra(Intent.EXTRA_TEXT, prep.message)
                                    }
                                    try {
                                        context.startActivity(smsIntent)
                                        voiceViewModel.markHandedOff("SMS")
                                    } catch (e: Exception) {
                                        val genericIntent = Intent(Intent.ACTION_VIEW).apply {
                                            data = Uri.parse("sms:${prep.smsNumbers}?body=${Uri.encode(prep.message)}")
                                        }
                                        try {
                                            context.startActivity(genericIntent)
                                            voiceViewModel.markHandedOff("SMS")
                                        } catch (ex: Exception) {}
                                    }
                                }
                                showConfirmDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.Sms, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.sos_open_sms), color = Color.Black)
                        }

                        Button(
                            onClick = {
                                val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:112")
                                }
                                try {
                                    context.startActivity(dialIntent)
                                    voiceViewModel.markHandedOff("DIALER")
                                } catch (e: Exception) {}
                                showConfirmDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AlertRed),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.Call, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.sos_call_emergency), color = Color.White)
                        }
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showConfirmDialog = false
                            voiceViewModel.resetHandOffState()
                        }
                    ) {
                        Text(stringResource(R.string.cancel), color = TextSecondary)
                    }
                }
            )
        }
    }
}

@Composable
private fun LanguageDropdownSelector(
    selectedCode: String,
    onSelectLanguage: (String) -> Unit
) {
    val languages = listOf(
        "en" to "English",
        "es" to "Español",
        "fr" to "Français",
        "de" to "Deutsch",
        "zh" to "中文(简体)",
        "ja" to "日本語",
        "th" to "ไทย",
        "hi" to "हिन्दी"
    )
    var expanded by remember { mutableStateOf(false) }
    val currentLabel = languages.find { it.first == selectedCode }?.second ?: "English"

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceVariantNavy)
        ) {
            Text("${stringResource(R.string.speech_language_spoken)}: $currentLabel")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(SurfaceNavy)
        ) {
            languages.forEach { (code, label) ->
                DropdownMenuItem(
                    text = { Text(label, color = TextPrimary) },
                    onClick = {
                        onSelectLanguage(code)
                        expanded = false
                    }
                )
            }
        }
    }
}
