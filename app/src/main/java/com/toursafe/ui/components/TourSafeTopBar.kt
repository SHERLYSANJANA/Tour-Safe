package com.toursafe.ui.components

import com.toursafe.R

import androidx.compose.ui.res.stringResource

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TourSafeTopBar(
    title: String,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    showNotificationIcon: Boolean = false,
    onNotificationClick: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.cd_back),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        actions = {
            if (showNotificationIcon) {
                IconButton(onClick = onNotificationClick) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = stringResource(R.string.notification_history_title),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}
