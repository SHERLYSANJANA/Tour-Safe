package com.toursafe.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.toursafe.ui.components.TourSafeBottomBar
import com.toursafe.ui.screens.*
import com.toursafe.viewmodels.*

@Composable
fun TourSafeNavGraph(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // ViewModels
    val homeViewModel: HomeViewModel = viewModel()
    val alertsViewModel: AlertsViewModel = viewModel()
    val accommodationViewModel: AccommodationViewModel = viewModel()
    val checkInViewModel: CheckInViewModel = viewModel()
    val settingsViewModel: SettingsViewModel = viewModel()

    val bottomBarRoutes = listOf(
        Screen.Home.route,
        Screen.Map.route,
        Screen.Alerts.route,
        Screen.SafeAccommodation.route,
        Screen.Profile.route
    )

    Scaffold(
        bottomBar = {
            if (currentRoute in bottomBarRoutes) {
                TourSafeBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // 1. Splash
            composable(Screen.Splash.route) {
                SplashScreen(
                    onSplashFinished = {
                        navController.navigate(Screen.Onboarding.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            // 2. Onboarding
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onContinue = {
                        navController.navigate(Screen.LanguageSelection.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            // 3. Language Selection
            composable(Screen.LanguageSelection.route) {
                LanguageSelectionScreen(
                    viewModel = settingsViewModel,
                    onLanguageConfirmed = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.LanguageSelection.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            // 4. Home Dashboard
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = homeViewModel,
                    onNavigate = { route -> navController.navigate(route) }
                )
            }

            // 5. Disaster Alerts
            composable(Screen.Alerts.route) {
                DisasterAlertsScreen(
                    viewModel = alertsViewModel,
                    onAlertClick = { alertId ->
                        navController.navigate(Screen.AlertDetails.createRoute(alertId))
                    }
                )
            }

            // 6. Alert Details
            composable(
                route = Screen.AlertDetails.route,
                arguments = listOf(navArgument("alertId") { type = NavType.StringType })
            ) { backStackEntry ->
                val alertId = backStackEntry.arguments?.getString("alertId") ?: ""
                val alert = alertsViewModel.getAlertById(alertId)
                AlertDetailsScreen(
                    alert = alert,
                    onBackClick = { navController.popBackStack() },
                    onNavigateToShelters = { navController.navigate(Screen.SafeAccommodation.route) }
                )
            }

            // 7. Map Screen
            composable(Screen.Map.route) {
                MapScreen(
                    onNavigateToShelters = { navController.navigate(Screen.SafeAccommodation.route) }
                )
            }

            // 8. Safe Accommodation Screen
            composable(Screen.SafeAccommodation.route) {
                SafeAccommodationScreen(
                    viewModel = accommodationViewModel,
                    onAccommodationClick = { accId ->
                        navController.navigate(Screen.AccommodationDetails.createRoute(accId))
                    }
                )
            }

            // 9. Accommodation Details
            composable(
                route = Screen.AccommodationDetails.route,
                arguments = listOf(navArgument("accommodationId") { type = NavType.StringType })
            ) { backStackEntry ->
                val accId = backStackEntry.arguments?.getString("accommodationId") ?: ""
                val acc = accommodationViewModel.getAccommodationById(accId)
                AccommodationDetailsScreen(
                    accommodation = acc,
                    onBackClick = { navController.popBackStack() },
                    onViewReviewsClick = { navController.navigate(Screen.Reviews.createRoute(accId)) }
                )
            }

            // 10. Reviews Screen
            composable(
                route = Screen.Reviews.route,
                arguments = listOf(navArgument("accommodationId") { type = NavType.StringType })
            ) { backStackEntry ->
                val accId = backStackEntry.arguments?.getString("accommodationId") ?: ""
                ReviewsScreen(
                    accommodationId = accId,
                    viewModel = accommodationViewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 11. Emergency Check-In
            composable(Screen.EmergencyCheckIn.route) {
                EmergencyCheckInScreen(
                    checkInViewModel = checkInViewModel,
                    homeViewModel = homeViewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 12. Emergency SOS
            composable(Screen.EmergencySos.route) {
                EmergencySosScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 13. Notification History
            composable(Screen.NotificationHistory.route) {
                NotificationHistoryScreen(
                    viewModel = settingsViewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 14. Settings Screen
            composable(Screen.Settings.route) {
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onBackClick = { navController.popBackStack() },
                    onNavigateToLanguage = { navController.navigate(Screen.LanguageSelection.route) }
                )
            }

            // 15. Profile Screen
            composable(Screen.Profile.route) {
                ProfileScreen(
                    viewModel = settingsViewModel,
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                )
            }
        }
    }
}
