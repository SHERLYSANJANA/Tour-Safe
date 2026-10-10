package com.toursafe.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat

data class UserGpsLocation(
    val latitude: Double,
    val longitude: Double,
    val accuracyMeters: Int,
    val isApproximate: Boolean = false
) {
    fun toGoogleMapsLink(): String = "https://maps.google.com/?q=$latitude,$longitude"
}

sealed class LocationState {
    object Idle : LocationState()
    object Loading : LocationState()
    data class Success(val location: UserGpsLocation) : LocationState()
    object PermissionDenied : LocationState()
    object GpsDisabled : LocationState()
    data class Error(val message: String) : LocationState()
}

class RealLocationManager(private val context: Context) {

    private val locationManager: LocationManager? by lazy {
        context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
    }

    fun isGpsEnabled(): Boolean {
        val lm = locationManager ?: return false
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        return fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED
    }

    fun isFineLocationGranted(): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    fun fetchCurrentLocation(onResult: (LocationState) -> Unit) {
        if (!hasLocationPermission()) {
            onResult(LocationState.PermissionDenied)
            return
        }

        if (!isGpsEnabled()) {
            onResult(LocationState.GpsDisabled)
            return
        }

        onResult(LocationState.Loading)

        val lm = locationManager
        if (lm == null) {
            onResult(LocationState.Error("Location service unavailable."))
            return
        }

        val isFine = isFineLocationGranted()

        // 1. Check last known location first for immediate response
        val gpsLast = try { lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) } catch (e: SecurityException) { null }
        val netLast = try { lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) } catch (e: SecurityException) { null }

        val bestLast = when {
            gpsLast != null && netLast != null -> if (gpsLast.time > netLast.time) gpsLast else netLast
            gpsLast != null -> gpsLast
            else -> netLast
        }

        if (bestLast != null && (System.currentTimeMillis() - bestLast.time) < 60000) {
            val gpsLoc = UserGpsLocation(
                latitude = bestLast.latitude,
                longitude = bestLast.longitude,
                accuracyMeters = bestLast.accuracy.toInt(),
                isApproximate = !isFine
            )
            onResult(LocationState.Success(gpsLoc))
            return
        }

        // 2. Request single fresh update
        val provider = when {
            lm.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
            lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
            else -> null
        }

        if (provider == null) {
            onResult(LocationState.GpsDisabled)
            return
        }

        val handler = Handler(Looper.getMainLooper())
        var hasReceivedLocation = false

        val listener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                if (!hasReceivedLocation) {
                    hasReceivedLocation = true
                    try { lm.removeUpdates(this) } catch (e: SecurityException) {}
                    val gpsLoc = UserGpsLocation(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        accuracyMeters = location.accuracy.toInt(),
                        isApproximate = !isFine
                    )
                    onResult(LocationState.Success(gpsLoc))
                }
            }

            @Deprecated("Deprecated in Java")
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {
                if (!hasReceivedLocation) {
                    hasReceivedLocation = true
                    try { lm.removeUpdates(this) } catch (e: SecurityException) {}
                    onResult(LocationState.GpsDisabled)
                }
            }
        }

        // 8 second timeout fallback
        handler.postDelayed({
            if (!hasReceivedLocation) {
                hasReceivedLocation = true
                try { lm.removeUpdates(listener) } catch (e: SecurityException) {}
                if (bestLast != null) {
                    val gpsLoc = UserGpsLocation(
                        latitude = bestLast.latitude,
                        longitude = bestLast.longitude,
                        accuracyMeters = bestLast.accuracy.toInt(),
                        isApproximate = true
                    )
                    onResult(LocationState.Success(gpsLoc))
                } else {
                    onResult(LocationState.Error("GPS location request timed out. Please try again."))
                }
            }
        }, 8000)

        try {
            lm.requestLocationUpdates(provider, 0L, 0f, listener, Looper.getMainLooper())
        } catch (e: SecurityException) {
            onResult(LocationState.PermissionDenied)
        } catch (e: Exception) {
            if (bestLast != null) {
                val gpsLoc = UserGpsLocation(
                    latitude = bestLast.latitude,
                    longitude = bestLast.longitude,
                    accuracyMeters = bestLast.accuracy.toInt(),
                    isApproximate = true
                )
                onResult(LocationState.Success(gpsLoc))
            } else {
                onResult(LocationState.Error(e.localizedMessage ?: "Failed to acquire location."))
            }
        }
    }
}
