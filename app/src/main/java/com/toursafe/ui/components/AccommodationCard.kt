package com.toursafe.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.toursafe.R
import com.toursafe.data.models.SafeAccommodation
import com.toursafe.ui.theme.*

@Composable
fun AccommodationCard(
    accommodation: SafeAccommodation,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceNavy)
            .border(1.dp, SurfaceVariantNavy, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (accommodation.isEmergencyShelter) Icons.Default.Shield else Icons.Default.Hotel,
                        contentDescription = null,
                        tint = if (accommodation.isEmergencyShelter) SafetyGreen else AccentCyan,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = accommodation.type,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (accommodation.isEmergencyShelter) SafetyGreen else AccentCyan
                    )
                }

                Text(
                    text = stringResource(R.string.km_away, accommodation.distanceKm),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            if (accommodation.isDemo) {
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
                text = accommodation.name,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )

            Text(
                text = accommodation.address,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (accommodation.rating != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        StarRatingBar(rating = accommodation.rating, starSize = 16.dp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.reviews_count, accommodation.rating, accommodation.reviewCount),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                } else {
                    Text(
                        text = "Rating Unlisted (Demo)",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }

                accommodation.pricePerNight?.let { price ->
                    Text(
                        text = price,
                        style = MaterialTheme.typography.titleSmall,
                        color = SafetyGreen
                    )
                }
            }

            if (accommodation.capacityAvailable != null && accommodation.maxCapacity != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.available_capacity, accommodation.capacityAvailable, accommodation.maxCapacity),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }
        }
    }
}
