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
    val isApproximate: Boolean = false,
    /** When the fix was taken (epoch millis), so the UI can show how fresh it is. */
    val timestampMs: Long = System.currentTimeMillis()
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
        val lm = locationManager
        if (lm == null) {
            onResult(LocationState.Error("Location service unavailable."))
            return
        }

        onResult(LocationState.Loading)

        val isFine = isFineLocationGranted()
        val bestLast = bestLastKnownLocation(lm)

        // 1. A very recent cached fix is good enough and instant.
        if (bestLast != null && ageMs(bestLast) < FRESH_LAST_KNOWN_MS) {
            onResult(LocationState.Success(toUserLocation(bestLast, isFine)))
            return
        }

        // 2. Ask every enabled provider at once (GPS chip AND network/Wi-Fi/cell) and take the
        //    first fix. Asking only the GPS chip fails indoors and during a cold start.
        val providers = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
            .filter { name -> try { lm.isProviderEnabled(name) } catch (e: Exception) { false } }
        if (providers.isEmpty()) {
            onResult(LocationState.GpsDisabled)
            return
        }

        val handler = Handler(Looper.getMainLooper())
        val listeners = mutableListOf<LocationListener>()
        var finished = false
        lateinit var timeout: Runnable

        // Delivers exactly one result, then stops every listener and the timeout.
        fun finish(state: LocationState) {
            if (finished) return
            finished = true
            handler.removeCallbacks(timeout)
            listeners.forEach { l -> try { lm.removeUpdates(l) } catch (e: Exception) { } }
            listeners.clear()
            onResult(state)
        }

        timeout = Runnable {
            val fallback = bestLast
            if (fallback != null && ageMs(fallback) < MAX_FALLBACK_AGE_MS) {
                // Only a recent cached position is ever reused; its age is carried in timestampMs.
                finish(LocationState.Success(toUserLocation(fallback, isFine, forceApproximate = true)))
            } else {
                finish(LocationState.Error("Could not get a GPS fix. Move to an open area and try again."))
            }
        }
        handler.postDelayed(timeout, REQUEST_TIMEOUT_MS)

        var registered = 0
        for (providerName in providers) {
            val listener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    finish(LocationState.Success(toUserLocation(location, isFine)))
                }

                @Deprecated("Deprecated in Java")
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {
                    if (!isGpsEnabled()) finish(LocationState.GpsDisabled)
                }
            }
            try {
                lm.requestLocationUpdates(providerName, 0L, 0f, listener, Looper.getMainLooper())
                listeners.add(listener)
                registered++
            } catch (e: SecurityException) {
                finish(LocationState.PermissionDenied)
                return
            } catch (e: Exception) {
                // This provider cannot be used; the other one may still work.
            }
        }
        if (registered == 0) {
            finish(LocationState.Error("Failed to start the location request."))
        }
    }

    private fun bestLastKnownLocation(lm: LocationManager): Location? {
        return listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
            .mapNotNull { name -> try { lm.getLastKnownLocation(name) } catch (e: Exception) { null } }
            .maxByOrNull { it.time }
    }

    private fun ageMs(location: Location): Long = System.currentTimeMillis() - location.time

    private fun toUserLocation(
        location: Location,
        isFine: Boolean,
        forceApproximate: Boolean = false
    ): UserGpsLocation {
        LocationUtils.lastUserLat = location.latitude
        LocationUtils.lastUserLng = location.longitude
        return UserGpsLocation(
            latitude = location.latitude,
            longitude = location.longitude,
            accuracyMeters = location.accuracy.toInt(),
            isApproximate = forceApproximate || !isFine || location.accuracy > APPROX_ACCURACY_METERS,
            timestampMs = if (location.time > 0L) location.time else System.currentTimeMillis()
        )
    }

    companion object {
        private const val FRESH_LAST_KNOWN_MS = 30_000L
        private const val MAX_FALLBACK_AGE_MS = 10 * 60_000L
        private const val REQUEST_TIMEOUT_MS = 20_000L
        private const val APPROX_ACCURACY_METERS = 200f
    }
}
