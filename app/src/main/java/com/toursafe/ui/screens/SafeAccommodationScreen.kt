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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.toursafe.R
import com.toursafe.data.repository.LoadStatus
import com.toursafe.ui.components.AccommodationCard
import com.toursafe.ui.components.DataStatusPanel
import com.toursafe.ui.components.PartialDataNote
import com.toursafe.ui.components.SourceAttribution
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

    var retryTick by remember { mutableStateOf(0) }

    LaunchedEffect(locationState, demoModeEnabled, retryTick) {
        val userLat = (locationState as? LocationState.Success)?.location?.latitude ?: 0.0
        val userLng = (locationState as? LocationState.Success)?.location?.longitude ?: 0.0
        viewModel.refreshData(userLat, userLng, demoModeEnabled)
    }

    val searchQuery by viewModel.searchQuery.collectAsState()
    val onlyShelters by viewModel.onlyShelters.collectAsState()
    val filteredList by viewModel.filteredAccommodations.collectAsState()
    val status by viewModel.status.collectAsState()

    Scaffold(
        topBar = {
            TourSafeTopBar(
                title = stringResource(R.string.safe_accommodations)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
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
                placeholder = { Text(stringResource(R.string.search_placeholder), color = MaterialTheme.colorScheme.onSurfaceVariant) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            // Shelter Filter Switch Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.show_free_shelters_only),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Switch(
                    checked = onlyShelters,
                    onCheckedChange = { viewModel.toggleShelterFilter(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                        checkedTrackColor = MaterialTheme.tourSafe.safe
                    )
                )
            }

            if (filteredList.isEmpty()) {
                DataStatusPanel(
                    status = status,
                    noneNearbyTitle = R.string.places_none_nearby_title,
                    noneNearbyDesc = R.string.places_none_nearby_desc,
                    onRetry = { retryTick++ }
                )
            } else {
                if (status == LoadStatus.PARTIAL) PartialDataNote()
                LazyColumn(
                    modifier = Modifier.weight(1f),
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

            if (!demoModeEnabled && (status == LoadStatus.LOADED || status == LoadStatus.PARTIAL)) {
                SourceAttribution(R.string.osm_attribution)
            }
        }
    }
}
