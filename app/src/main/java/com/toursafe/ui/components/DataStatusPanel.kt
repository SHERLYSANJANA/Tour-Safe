package com.toursafe.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.toursafe.R
import com.toursafe.data.repository.LoadStatus

/**
 * Honest empty/loading/error panel for a list backed by live data.
 *
 * An empty list can mean five different things, and the user must be able to tell them apart:
 *  - no location yet            -> [LoadStatus.IDLE]
 *  - still loading              -> [LoadStatus.LOADING]
 *  - sources answered, none near -> [LoadStatus.LOADED] / [LoadStatus.PARTIAL]
 *  - could not reach sources    -> [LoadStatus.UNAVAILABLE] (never presented as "nothing nearby")
 *
 * Uses MaterialTheme colours only, so it follows the app theme.
 */
@Composable
fun DataStatusPanel(
    status: LoadStatus,
    @StringRes noneNearbyTitle: Int,
    @StringRes noneNearbyDesc: Int,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null
) {
    val (title, desc) = when (status) {
        LoadStatus.IDLE -> R.string.data_waiting_location_title to R.string.data_waiting_location_desc
        LoadStatus.LOADING -> R.string.data_loading_title to R.string.data_loading_desc
        LoadStatus.LOADED, LoadStatus.PARTIAL -> noneNearbyTitle to noneNearbyDesc
        LoadStatus.UNAVAILABLE -> R.string.data_unavailable_title to R.string.data_unavailable_desc
    }

    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, shape)
            .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, shape)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (status == LoadStatus.LOADING) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            }
            Text(
                text = stringResource(title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(desc),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (status == LoadStatus.PARTIAL) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.data_partial_note),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (status == LoadStatus.UNAVAILABLE && onRetry != null) {
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(onClick = onRetry) {
                Text(stringResource(R.string.data_retry))
            }
        }
    }
}

/** Shown above a non-empty list when one of several sources could not be reached. */
@Composable
fun PartialDataNote(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.data_partial_note),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.padding(bottom = 8.dp)
    )
}

/** Small footer for required data attribution (OpenStreetMap) or source credit (USGS / GDACS). */
@Composable
fun SourceAttribution(@StringRes textRes: Int, modifier: Modifier = Modifier) {
    Text(
        text = stringResource(textRes),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    )
}
