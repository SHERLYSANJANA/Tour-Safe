package com.toursafe.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.toursafe.data.models.PropertyReview
import com.toursafe.ui.components.StarRatingBar
import com.toursafe.ui.components.TourSafeTopBar
import com.toursafe.ui.theme.*
import com.toursafe.viewmodels.AccommodationViewModel

@Composable
fun ReviewsScreen(
    accommodationId: String,
    viewModel: AccommodationViewModel,
    onBackClick: () -> Unit
) {
    val reviews by viewModel.reviews.collectAsState()
    val filteredReviews = reviews.filter { it.accommodationId == accommodationId || accommodationId.isBlank() }
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TourSafeTopBar(
                title = "Verified Reviews",
                showBackButton = true,
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = AccentCyan,
                contentColor = Color.Black
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Review")
            }
        },
        containerColor = BackgroundNavy
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredReviews) { rev ->
                    ReviewCard(review = rev)
                }
            }
        }

        if (showDialog) {
            AddReviewDialog(
                onDismiss = { showDialog = false },
                onSubmit = { name, rating, comment ->
                    viewModel.addReview(accommodationId, name, rating, comment)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
private fun ReviewCard(review: PropertyReview) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceNavy, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = review.userName,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                    Text(
                        text = "Verified Traveler • ${review.userCountry}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
                Text(
                    text = review.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            StarRatingBar(rating = review.rating, starSize = 16.dp)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = review.comment,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary
            )
        }
    }
}

@Composable
private fun AddReviewDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, Double, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var comment by remember { mutableStateOf("") }
    var userRating by remember { mutableStateOf(5.0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceNavy,
        title = { Text("Write Safety Review", color = TextPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Your Name", color = TextSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary
                    )
                )

                Text("Rating:", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                StarRatingBar(
                    rating = userRating,
                    starSize = 28.dp,
                    onRatingChanged = { userRating = it }
                )

                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Safety Comments & Experience", color = TextSecondary) },
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && comment.isNotBlank()) {
                        onSubmit(name, userRating, comment)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AccentCyan)
            ) {
                Text("Submit Review", color = Color.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}
