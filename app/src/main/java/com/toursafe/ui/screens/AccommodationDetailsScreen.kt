package com.toursafe.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.toursafe.R
import com.toursafe.data.models.SafeAccommodation
import com.toursafe.ui.components.StarRatingBar
import com.toursafe.ui.components.TourSafeTopBar
import com.toursafe.ui.theme.*
import com.toursafe.util.LocationState
import com.toursafe.util.LocationUtils
import com.toursafe.viewmodels.AccommodationViewModel
import com.toursafe.viewmodels.GpsLocationViewModel

@Composable
fun AccommodationDetailsScreen(
    accommodation: SafeAccommodation?,
    viewModel: AccommodationViewModel = viewModel(),
    locationViewModel: GpsLocationViewModel = viewModel(),
    onBackClick: () -> Unit,
    onViewReviewsClick: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        locationViewModel.initLocationManager(context)
        if (locationViewModel.hasPermission()) {
            locationViewModel.fetchLocation()
        }
    }

    val locationState by locationViewModel.locationState.collectAsState()

    LaunchedEffect(locationState) {
        if (locationState is LocationState.Success) {
            val loc = (locationState as LocationState.Success).location
            viewModel.updateGpsLocation(loc.latitude, loc.longitude)
        }
    }

    val currentAcc = accommodation?.let { viewModel.getAccommodationById(it.id) } ?: accommodation

    Scaffold(
        topBar = {
            TourSafeTopBar(
                title = stringResource(R.string.accommodation_details),
                showBackButton = true,
                onBackClick = onBackClick
            )
        },
        containerColor = BackgroundNavy
    ) { padding ->
        if (currentAcc == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.accommodation_not_found),
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Main Info Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceNavy)
                        .border(1.dp, SurfaceVariantNavy, RoundedCornerShape(16.dp))
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = currentAcc.type,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (currentAcc.isEmergencyShelter) SafetyGreen else AccentCyan
                            )
                            Text(
                                text = stringResource(R.string.km_away, currentAcc.distanceKm),
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }

                        if (currentAcc.isDemo) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(WarningAmberBg)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Unverified Demo Record",
                                    color = WarningAmber,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = currentAcc.name,
                            style = MaterialTheme.typography.headlineMedium,
                            color = TextPrimary
                        )

                        Text(
                            text = currentAcc.address,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )

                        if (currentAcc.rating != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                StarRatingBar(rating = currentAcc.rating)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.reviews_count, currentAcc.rating, currentAcc.reviewCount),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                            }
                        }

                        currentAcc.pricePerNight?.let { price ->
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = stringResource(R.string.rate_label, price),
                                style = MaterialTheme.typography.titleLarge,
                                color = SafetyGreen
                            )
                        }
                    }
                }

                // Emergency & Safety Amenities
                if (currentAcc.emergencyAmenities.isNotEmpty()) {
                    Text(
                        text = stringResource(R.string.resilience_amenities_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        currentAcc.emergencyAmenities.forEach { amenity ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(SurfaceNavy, RoundedCornerShape(12.dp))
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = SafetyGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = amenity,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }

                // Capacity & Contact Panel
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceNavy, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        if (currentAcc.capacityAvailable != null && currentAcc.maxCapacity != null) {
                            Text(
                                text = stringResource(R.string.available_capacity, currentAcc.capacityAvailable, currentAcc.maxCapacity),
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary
                            )
                        } else {
                            Text(
                                text = "Capacity Unlisted (Demo)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextMuted
                            )
                        }

                        if (currentAcc.contactPhone != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = stringResource(R.string.emergency_phone_label, currentAcc.contactPhone),
                                style = MaterialTheme.typography.bodyMedium,
                                color = AccentCyan
                            )
                        }
                    }
                }

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onViewReviewsClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceVariantNavy),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.reviews),
                            color = TextPrimary,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Button(
                        onClick = {
                            LocationUtils.openMapsDirections(
                                context = context,
                                destinationLat = currentAcc.latitude,
                                destinationLng = currentAcc.longitude,
                                destinationName = currentAcc.name,
                                isDemoRecord = currentAcc.isDemo
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = if (currentAcc.isDemo) SurfaceVariantNavy else AccentCyan),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Directions,
                            contentDescription = null,
                            tint = if (currentAcc.isDemo) TextMuted else Color.Black
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.directions),
                            color = if (currentAcc.isDemo) TextMuted else Color.Black,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}
