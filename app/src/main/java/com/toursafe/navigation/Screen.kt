package com.toursafe.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object LanguageSelection : Screen("language_selection")
    object Home : Screen("home")
    object Alerts : Screen("alerts")
    object AlertDetails : Screen("alert_details/{alertId}") {
        fun createRoute(alertId: String) = "alert_details/$alertId"
    }
    object Map : Screen("map")
    object SafeAccommodation : Screen("safe_accommodation")
    object AccommodationDetails : Screen("accommodation_details/{accommodationId}") {
        fun createRoute(accommodationId: String) = "accommodation_details/$accommodationId"
    }
    object Reviews : Screen("reviews/{accommodationId}") {
        fun createRoute(accommodationId: String) = "reviews/$accommodationId"
    }
    object EmergencyCheckIn : Screen("emergency_check_in")
    object EmergencySos : Screen("emergency_sos")
    object NotificationHistory : Screen("notification_history")
    object Settings : Screen("settings")
    object Profile : Screen("profile")
}
