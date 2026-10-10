package com.toursafe.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.toursafe.ui.components.AccommodationCard
import com.toursafe.ui.components.TourSafeTopBar
import com.toursafe.ui.theme.*
import com.toursafe.viewmodels.AccommodationViewModel

@Composable
fun SafeAccommodationScreen(
    viewModel: AccommodationViewModel,
    onAccommodationClick: (String) -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val onlyShelters by viewModel.onlyShelters.collectAsState()
    val filteredList = viewModel.getFilteredAccommodations()

    Scaffold(
        topBar = {
            TourSafeTopBar(
                title = "Safe Accommodations"
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
                placeholder = { Text("Search safe hotel, shelter, location...", color = TextMuted) },
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
                    text = "Show Emergency Free Shelters Only",
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

            // Accommodations List
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
