package com.toursafe.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.toursafe.R
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
                title = stringResource(R.string.disaster_action_plan),
                showBackButton = true,
                onBackClick = onBackClick
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (alert == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.alert_not_found),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            val (badgeBg, badgeColor, severityLabel) = when (alert.severity) {
                AlertSeverity.CRITICAL -> Triple(MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.error, stringResource(R.string.severity_critical))
                AlertSeverity.WARNING -> Triple(MaterialTheme.tourSafe.warningContainer, MaterialTheme.tourSafe.warning, stringResource(R.string.severity_warning))
                AlertSeverity.INFO -> Triple(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.primary, stringResource(R.string.severity_info))
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
                                text = stringResource(R.string.alert_header, severityLabel),
                                style = MaterialTheme.typography.titleMedium,
                                color = badgeColor
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = alert.title,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = alert.sourceLabel,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.tourSafe.warning
                        )
                    }
                }

                // Description Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.affected_area_label, alert.affectedArea),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.distance_from_location, alert.distanceKm),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = alert.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Official Instructions Section
                Text(
                    text = stringResource(R.string.official_instructions_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    alert.officialInstructions.forEach { instruction ->
                        InstructionRow(
                            text = instruction,
                            iconColor = MaterialTheme.colorScheme.error
                        )
                    }
                }

                // Recommended Actions
                Text(
                    text = stringResource(R.string.recommended_actions_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    alert.recommendedActions.forEach { action ->
                        InstructionRow(
                            text = action,
                            iconColor = MaterialTheme.tourSafe.safe
                        )
                    }
                }

                Button(
                    onClick = onNavigateToShelters,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.find_safe_places_near_you),
                        color = MaterialTheme.colorScheme.onPrimary,
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
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
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
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
