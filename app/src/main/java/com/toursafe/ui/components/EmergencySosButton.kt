package com.toursafe.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sos
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.toursafe.R
import com.toursafe.ui.theme.Dimens

/**
 * The one SOS entry point style. It only navigates to the SOS screen, which keeps its own
 * review/confirm step: pressing this never sends, shares or dials anything by itself.
 * Red is used here because this is an emergency action.
 */
@Composable
fun EmergencySosButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String = stringResource(R.string.emergency_sos)
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = Dimens.EmergencyTouchTarget),
        shape = MaterialTheme.shapes.large,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = MaterialTheme.colorScheme.onError
        )
    ) {
        Icon(
            imageVector = Icons.Default.Sos,
            contentDescription = null,
            modifier = Modifier.size(Dimens.IconLg)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = label, style = MaterialTheme.typography.titleLarge)
    }
}
