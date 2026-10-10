package com.toursafe.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.toursafe.data.models.SafetyLevel
import com.toursafe.ui.components.TourSafeTopBar
import com.toursafe.ui.theme.*
import com.toursafe.viewmodels.CheckInViewModel
import com.toursafe.viewmodels.HomeViewModel

@Composable
fun EmergencyCheckInScreen(
    checkInViewModel: CheckInViewModel,
    homeViewModel: HomeViewModel,
    onBackClick: () -> Unit
) {
    val selectedStatus by checkInViewModel.selectedStatus.collectAsState()
    val customNote by checkInViewModel.customNote.collectAsState()
    val lastBroadcast by checkInViewModel.lastBroadcastMessage.collectAsState()

    Scaffold(
        topBar = {
            TourSafeTopBar(
                title = "Emergency Check-In",
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
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "Broadcast your safety status instantly to emergency contacts and local authorities.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )

                // Option 1: I'm Safe
                StatusOptionCard(
                    title = "I'm Safe",
                    subtitle = "I am in a safe location and do not need assistance.",
                    icon = Icons.Default.CheckCircle,
                    color = SafetyGreen,
                    bgColor = SafetyGreenBg,
                    isSelected = selectedStatus == SafetyLevel.SAFE,
                    onClick = { checkInViewModel.selectStatus(SafetyLevel.SAFE) }
                )

                // Option 2: Moving to Safety
                StatusOptionCard(
                    title = "Moving to Safety",
                    subtitle = "I am currently evacuating or travelling to safe ground.",
                    icon = Icons.AutoMirrored.Filled.DirectionsRun,
                    color = WarningAmber,
                    bgColor = WarningAmberBg,
                    isSelected = selectedStatus == SafetyLevel.MOVING_TO_SAFETY,
                    onClick = { checkInViewModel.selectStatus(SafetyLevel.MOVING_TO_SAFETY) }
                )

                // Option 3: I Need Help
                StatusOptionCard(
                    title = "I Need Help",
                    subtitle = "I require emergency rescue or immediate medical aid.",
                    icon = Icons.Default.Warning,
                    color = AlertRed,
                    bgColor = AlertRedBg,
                    isSelected = selectedStatus == SafetyLevel.NEED_HELP,
                    onClick = { checkInViewModel.selectStatus(SafetyLevel.NEED_HELP) }
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = customNote,
                    onValueChange = { checkInViewModel.updateCustomNote(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Add optional note (e.g. Hotel Room 302, 2 people)", color = TextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceNavy,
                        unfocusedContainerColor = SurfaceNavy,
                        focusedBorderColor = AccentCyan,
                        unfocusedBorderColor = SurfaceVariantNavy,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(14.dp),
                    maxLines = 3
                )

                lastBroadcast?.let { msg ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceVariantNavy, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = msg,
                            style = MaterialTheme.typography.bodySmall,
                            color = SafetyGreen
                        )
                    }
                }
            }

            Button(
                onClick = {
                    checkInViewModel.submitCheckIn {
                        homeViewModel.updateSafetyLevel(selectedStatus, customNote.ifBlank { null })
                        onBackClick()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Broadcast Status Now",
                    color = Color.Black,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
private fun StatusOptionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    bgColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isSelected) bgColor else SurfaceNavy, RoundedCornerShape(16.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) color else SurfaceVariantNavy,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}
