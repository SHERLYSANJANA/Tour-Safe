package com.toursafe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.toursafe.navigation.TourSafeNavGraph
import com.toursafe.ui.theme.TourSafeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TourSafeTheme {
                TourSafeNavGraph()
            }
        }
    }
}
