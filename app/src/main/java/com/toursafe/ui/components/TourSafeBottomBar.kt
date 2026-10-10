package com.toursafe.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.toursafe.navigation.Screen
import com.toursafe.ui.theme.AccentCyan
import com.toursafe.ui.theme.SurfaceNavy
import com.toursafe.ui.theme.TextMuted
import com.toursafe.ui.theme.TextPrimary

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Home : BottomNavItem(Screen.Home.route, "Home", Icons.Default.Home)
    object Map : BottomNavItem(Screen.Map.route, "Map", Icons.Default.Map)
    object Alerts : BottomNavItem(Screen.Alerts.route, "Alerts", Icons.Default.Warning)
    object SafePlaces : BottomNavItem(Screen.SafeAccommodation.route, "Safe Places", Icons.Default.Hotel)
    object Profile : BottomNavItem(Screen.Profile.route, "Profile", Icons.Default.Person)
}

@Composable
fun TourSafeBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Map,
        BottomNavItem.Alerts,
        BottomNavItem.SafePlaces,
        BottomNavItem.Profile
    )

    NavigationBar(
        containerColor = SurfaceNavy,
        contentColor = TextPrimary
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = if (isSelected) AccentCyan else TextMuted
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        color = if (isSelected) AccentCyan else TextMuted,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = SurfaceNavy
                )
            )
        }
    }
}
