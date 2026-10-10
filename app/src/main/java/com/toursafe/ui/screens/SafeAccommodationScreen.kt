package com.toursafe.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.toursafe.R
import com.toursafe.ui.components.AccommodationCard
import com.toursafe.ui.components.TourSafeTopBar
import com.toursafe.ui.theme.*
import com.toursafe.util.LocationState
import com.toursafe.viewmodels.AccommodationViewModel
import com.toursafe.viewmodels.GpsLocationViewModel
import com.toursafe.viewmodels.SettingsViewModel

@Composable
fun SafeAccommodationScreen(
    viewModel: AccommodationViewModel,
    locationViewModel: GpsLocationViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel(),
    onAccommodationClick: (String) -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        settingsViewModel.loadSettings(context)
        locationViewModel.initLocationManager(context)
        if (locationViewModel.hasPermission()) {
            locationViewModel.fetchLocation()
        }
    }

    val locationState by locationViewModel.locationState.collectAsState()
    val demoModeEnabled by settingsViewModel.demoModeEnabled.collectAsState()

    LaunchedEffect(locationState, demoModeEnabled) {
        val userLat = (locationState as? LocationState.Success)?.location?.latitude ?: 0.0
        val userLng = (locationState as? LocationState.Success)?.location?.longitude ?: 0.0
        viewModel.refreshData(userLat, userLng, demoModeEnabled)
    }

    val searchQuery by viewModel.searchQuery.collectAsState()
    val onlyShelters by viewModel.onlyShelters.collectAsState()
    val filteredList = viewModel.getFilteredAccommodations()

    Scaffold(
        topBar = {
            TourSafeTopBar(
                title = stringResource(R.string.safe_accommodations)
            )
        },
        containerColor = BackgroundNavy
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Search Input Field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.search_placeholder), color = TextMuted) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = AccentCyan
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SurfaceNavy,
                    unfocusedContainerColor = SurfaceNavy,
                    focusedBorderColor = AccentCyan,
                    unfocusedBorderColor = SurfaceVariantNavy,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            // Shelter Filter Switch Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceNavy, RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.show_free_shelters_only),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary
                )
                Switch(
                    checked = onlyShelters,
                    onCheckedChange = { viewModel.toggleShelterFilter(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        checkedTrackColor = SafetyGreen
                    )
                )
            }

            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceNavy, RoundedCornerShape(16.dp))
                        .border(1.dp, SurfaceVariantNavy, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.no_verified_source_title),
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.no_verified_source_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredList) { acc ->
                        AccommodationCard(
                            accommodation = acc,
                            onClick = { onAccommodationClick(acc.id) }
                        )
                    }
                }
            }
        }
    }
}
