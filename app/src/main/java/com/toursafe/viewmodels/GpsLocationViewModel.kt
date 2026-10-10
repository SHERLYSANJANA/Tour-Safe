package com.toursafe.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.toursafe.util.LocationState
import com.toursafe.util.RealLocationManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GpsLocationViewModel : ViewModel() {

    private var locationManager: RealLocationManager? = null

    private val _locationState = MutableStateFlow<LocationState>(LocationState.Idle)
    val locationState: StateFlow<LocationState> = _locationState.asStateFlow()

    fun initLocationManager(context: Context) {
        if (locationManager == null) {
            locationManager = RealLocationManager(context)
        }
    }

    fun hasPermission(): Boolean {
        return locationManager?.hasLocationPermission() == true
    }

    fun fetchLocation() {
        _locationState.value = LocationState.Loading
        locationManager?.fetchCurrentLocation { state ->
            _locationState.value = state
        }
    }

    fun refreshLocation() {
        fetchLocation()
    }
}
