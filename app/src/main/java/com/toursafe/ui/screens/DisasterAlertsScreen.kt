package com.toursafe.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.toursafe.R
import com.toursafe.data.models.AlertSeverity
import com.toursafe.navigation.Screen
import com.toursafe.ui.components.AlertCard
import com.toursafe.ui.components.TourSafeTopBar
import com.toursafe.ui.theme.*
import com.toursafe.viewmodels.AlertsViewModel

@Composable
fun DisasterAlertsScreen(
    viewModel: AlertsViewModel,
    onAlertClick: (String) -> Unit
) {
    val filteredAlerts by viewModel.filteredAlerts.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()

    Scaffold(
        topBar = {
            TourSafeTopBar(
                title = stringResource(R.string.active_disaster_alerts)
            )
        },
        containerColor = BackgroundNavy
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedFilter == null,
                        onClick = { viewModel.filterBySeverity(null) },
                        label = { Text(stringResource(R.string.view_all)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AccentCyan,
                            selectedLabelColor = Color.Black
                        )
                    )
                }
                item {
                    FilterChip(
                        selected = selectedFilter == AlertSeverity.CRITICAL,
                        onClick = { viewModel.filterBySeverity(AlertSeverity.CRITICAL) },
                        label = { Text(stringResource(R.string.severity_critical)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AlertRed,
                            selectedLabelColor = Color.White
                        )
                    )
                }
                item {
                    FilterChip(
                        selected = selectedFilter == AlertSeverity.WARNING,
                        onClick = { viewModel.filterBySeverity(AlertSeverity.WARNING) },
                        label = { Text(stringResource(R.string.severity_warning)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = WarningAmber,
                            selectedLabelColor = Color.Black
                        )
                    )
                }
                item {
                    FilterChip(
                        selected = selectedFilter == AlertSeverity.ADVISORY,
                        onClick = { viewModel.filterBySeverity(AlertSeverity.ADVISORY) },
                        label = { Text(stringResource(R.string.severity_advisory)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SurfaceVariantNavy,
                            selectedLabelColor = TextPrimary
                        )
                    )
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredAlerts) { alert ->
                    AlertCard(
                        alert = alert,
                        onClick = { onAlertClick(alert.id) }
                    )
                }
            }
        }
    }
}
