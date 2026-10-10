package com.toursafe.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.toursafe.ui.components.TourSafeTopBar
import com.toursafe.ui.theme.*

@Composable
fun EmergencySosScreen(
    onBackClick: () -> Unit
) {
    var showConfirmDialog by remember { mutableStateOf(false) }
    var sosSent by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TourSafeTopBar(
                title = "Emergency SOS",
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "DISASTER SOS ASSISTANCE",
                    style = MaterialTheme.typography.labelSmall,
                    color = AlertRed
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Press and hold the button below to dispatch your real-time coordinates to emergency responders and primary contacts.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

            // Big Prominent SOS Button
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(if (sosSent) SafetyGreenBg else AlertRedBg)
                    .border(4.dp, if (sosSent) SafetyGreen else AlertRed, CircleShape)
                    .clickable { showConfirmDialog = true },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "SOS",
                        tint = if (sosSent) SafetyGreen else AlertRed,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (sosSent) "SIGNAL SENT" else "TAP FOR SOS",
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextPrimary
                    )
                }
            }

            // Explanation box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceNavy, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "📍 Your Shared GPS Coordinates:",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "7.8804° N, 98.3923° E (Phuket Old Town)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AccentCyan
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Your device will automatically transmit an emergency beacon with location data every 2 minutes.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }

        if (showConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmDialog = false },
                containerColor = SurfaceNavy,
                title = { Text("Confirm Emergency SOS Signal?", color = AlertRed) },
                text = {
                    Text(
                        "This will immediately transmit your emergency beacon and coordinates to local disaster response teams and emergency contacts.",
                        color = TextPrimary
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            sosSent = true
                            showConfirmDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AlertRed)
                    ) {
                        Text("CONFIRM SOS", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmDialog = false }) {
                        Text("Cancel", color = TextSecondary)
                    }
                }
            )
        }
    }
}
