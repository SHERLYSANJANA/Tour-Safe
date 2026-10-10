package com.toursafe.util

import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.widget.Toast
import java.util.Locale
import kotlin.math.roundToInt

object LocationUtils {

    var lastUserLat: Double = 0.0
    var lastUserLng: Double = 0.0

    fun calculateDistanceKm(
        userLat: Double,
        userLng: Double,
        targetLat: Double,
        targetLng: Double
    ): Double {
        if (userLat == 0.0 && userLng == 0.0) return 0.0
        val results = FloatArray(1)
        Location.distanceBetween(userLat, userLng, targetLat, targetLng, results)
        val meters = results[0]
        val km = meters / 1000.0
        return (km * 10).roundToInt() / 10.0
    }

    fun reverseGeocodeLocationName(
        context: Context,
        lat: Double,
        lng: Double,
        onResult: (String) -> Unit
    ) {
        val defaultFormatted = "Lat: ${"%.4f".format(lat)}, Lng: ${"%.4f".format(lng)}"
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(lat, lng, 1) { addresses ->
                    val addr = addresses.firstOrNull()
                    val cityName = addr?.locality ?: addr?.subAdminArea ?: addr?.adminArea ?: defaultFormatted
                    onResult(cityName)
                }
            } else {
                @Suppress("DEPRECATION")
                val addresses: List<Address>? = geocoder.getFromLocation(lat, lng, 1)
                val addr = addresses?.firstOrNull()
                val cityName = addr?.locality ?: addr?.subAdminArea ?: addr?.adminArea ?: defaultFormatted
                onResult(cityName)
            }
        } catch (e: Exception) {
            onResult(defaultFormatted)
        }
    }

    fun openMapsDirections(
        context: Context,
        destinationLat: Double,
        destinationLng: Double,
        destinationName: String = "Emergency Shelter",
        isDemoRecord: Boolean = false
    ): Boolean {
        if (isDemoRecord) {
            Toast.makeText(context, "Directions are disabled for unverified demo records.", Toast.LENGTH_SHORT).show()
            return false
        }

        val encodedName = Uri.encode(destinationName)
        val gmmIntentUri = Uri.parse("google.navigation:q=$destinationLat,$destinationLng")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
            setPackage("com.google.android.apps.maps")
        }

        try {
            if (mapIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(mapIntent)
                return true
            } else {
                val webUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$destinationLat,$destinationLng")
                val webIntent = Intent(Intent.ACTION_VIEW, webUri)
                context.startActivity(webIntent)
                return true
            }
        } catch (e: Exception) {
            val genericUri = Uri.parse("geo:$destinationLat,$destinationLng?q=$destinationLat,$destinationLng($encodedName)")
            try {
                context.startActivity(Intent(Intent.ACTION_VIEW, genericUri))
                return true
            } catch (ex: Exception) {
                val webUrl = "https://www.google.com/maps/search/?api=1&query=$destinationLat,$destinationLng"
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(webUrl)))
                return true
            }
        }
    }
}
