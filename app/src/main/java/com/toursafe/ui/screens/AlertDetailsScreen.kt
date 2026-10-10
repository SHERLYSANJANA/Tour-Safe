package com.toursafe.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.toursafe.data.models.AlertSeverity
import com.toursafe.data.models.DisasterAlert
import com.toursafe.ui.components.TourSafeTopBar
import com.toursafe.ui.theme.*

@Composable
fun AlertDetailsScreen(
    alert: DisasterAlert?,
    onBackClick: () -> Unit,
    onNavigateToShelters: () -> Unit
) {
    Scaffold(
        topBar = {
            TourSafeTopBar(
                title = "Disaster Action Plan",
                showBackButton = true,
                onBackClick = onBackClick
            )
        },
        containerColor = BackgroundNavy
    ) { padding ->
        if (alert == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Alert details not found.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
            }
        } else {
            val (badgeBg, badgeColor) = when (alert.severity) {
                AlertSeverity.CRITICAL -> Pair(AlertRedBg, AlertRed)
                AlertSeverity.WARNING -> Pair(WarningAmberBg, WarningAmber)
                else -> Pair(SurfaceVariantNavy, AccentCyan)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Severity Header Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(badgeBg)
                        .border(1.dp, badgeColor, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = badgeColor,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = alert.severity.name + " ALERT",
                                style = MaterialTheme.typography.titleMedium,
                                color = badgeColor
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = alert.title,
                            style = MaterialTheme.typography.headlineMedium,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = alert.sourceLabel,
                            style = MaterialTheme.typography.labelMedium,
                            color = WarningAmber
                        )
                    }
                }

                // Description Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceNavy, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = "Affected Area: ${alert.affectedArea}",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Distance: ${alert.distanceKm} km from current location",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = alert.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextPrimary
                        )
                    }
                }

                // Official Instructions Section
                Text(
                    text = "Official Emergency Instructions",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    alert.officialInstructions.forEach { instruction ->
                        InstructionRow(
                            text = instruction,
                            iconColor = AlertRed
                        )
                    }
                }

                // Recommended Actions
                Text(
                    text = "Recommended Tourist Actions",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    alert.recommendedActions.forEach { action ->
                        InstructionRow(
                            text = action,
                            iconColor = SafetyGreen
                        )
                    }
                }

                Button(
                    onClick = onNavigateToShelters,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Find Safe Places Near You",
                        color = Color.Black,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun InstructionRow(
    text: String,
    iconColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceNavy, RoundedCornerShape(12.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier
                .size(20.dp)
                .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary
        )
    }
}
