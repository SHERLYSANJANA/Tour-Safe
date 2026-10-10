package com.toursafe.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.toursafe.data.models.AlertSeverity
import com.toursafe.data.models.DisasterAlert
import com.toursafe.ui.theme.AccentCyan
import com.toursafe.ui.theme.AlertRed
import com.toursafe.ui.theme.AlertRedBg
import com.toursafe.ui.theme.SurfaceNavy
import com.toursafe.ui.theme.SurfaceVariantNavy
import com.toursafe.ui.theme.TextMuted
import com.toursafe.ui.theme.TextPrimary
import com.toursafe.ui.theme.TextSecondary
import com.toursafe.ui.theme.WarningAmber
import com.toursafe.ui.theme.WarningAmberBg

@Composable
fun AlertCard(
    alert: DisasterAlert,
    onClick: () -> Unit
) {
    val (badgeBg, badgeText, badgeColor) = when (alert.severity) {
        AlertSeverity.CRITICAL -> Triple(AlertRedBg, "CRITICAL", AlertRed)
        AlertSeverity.WARNING -> Triple(WarningAmberBg, "WARNING", WarningAmber)
        AlertSeverity.ADVISORY -> Triple(SurfaceVariantNavy, "ADVISORY", AccentCyan)
        AlertSeverity.INFO -> Triple(SurfaceVariantNavy, "INFO", TextSecondary)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceNavy)
            .border(1.dp, badgeColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(badgeBg)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = badgeText,
                        color = badgeColor,
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                Text(
                    text = "${alert.distanceKm} km away",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = alert.title,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = alert.affectedArea,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = alert.sourceLabel,
                style = MaterialTheme.typography.labelSmall,
                color = WarningAmber
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = alert.timestamp,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "View Action Plan",
                        style = MaterialTheme.typography.labelMedium,
                        color = AccentCyan
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = AccentCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
