package com.toursafe

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.toursafe.navigation.TourSafeNavGraph
import com.toursafe.ui.theme.TourSafeTheme
import com.toursafe.util.LocaleManager
import com.toursafe.viewmodels.SettingsViewModel

class MainActivity : ComponentActivity() {

    // Same activity-scoped instance the NavGraph obtains via viewModel().
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleManager.wrapContext(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        super.onCreate(savedInstanceState)
        settingsViewModel.loadSettings(applicationContext)
        setContent {
            val themeMode by settingsViewModel.themeMode.collectAsState()
            val systemDark = isSystemInDarkTheme()
            val darkTheme = when (themeMode) {
                SettingsViewModel.THEME_DARK -> true
                SettingsViewModel.THEME_LIGHT -> false
                else -> systemDark
            }
            // Keep status/navigation bar icon contrast in sync with the in-app theme choice.
            DisposableEffect(darkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { darkTheme },
                    navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { darkTheme }
                )
                onDispose { }
            }
            TourSafeTheme(darkTheme = darkTheme) {
                TourSafeNavGraph()
            }
        }
    }
}
