package com.toursafe.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.toursafe.R
import com.toursafe.navigation.Screen

sealed class BottomNavItem(
    val route: String,
    val titleRes: Int,
    val icon: ImageVector,
    val isEmergency: Boolean = false
) {
    object Home : BottomNavItem(Screen.Home.route, R.string.nav_home, Icons.Default.Home)
    object Map : BottomNavItem(Screen.Map.route, R.string.nav_map, Icons.Default.Map)
    // Emergency access is always one tap away from every main screen.
    object Sos : BottomNavItem(Screen.EmergencySos.route, R.string.nav_sos, Icons.Default.Sos, isEmergency = true)
    object Alerts : BottomNavItem(Screen.Alerts.route, R.string.nav_alerts, Icons.Default.Warning)
    object Profile : BottomNavItem(Screen.Profile.route, R.string.nav_profile, Icons.Default.Person)
}

@Composable
fun TourSafeBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Map,
        BottomNavItem.Sos,
        BottomNavItem.Alerts,
        BottomNavItem.Profile
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route
            val titleText = stringResource(item.titleRes)
            val accent = if (item.isEmergency) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            val idle = if (item.isEmergency) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null
                    )
                },
                label = {
                    Text(
                        text = titleText,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = accent,
                    selectedTextColor = accent,
                    unselectedIconColor = idle,
                    unselectedTextColor = idle,
                    indicatorColor = if (item.isEmergency) MaterialTheme.colorScheme.errorContainer
                    else MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}
