package com.toursafe

import com.toursafe.data.models.AlertSeverity
import com.toursafe.data.models.DisasterAlert
import com.toursafe.data.models.MarkerType
import com.toursafe.data.models.SafeAccommodation
import com.toursafe.data.repository.LoadStatus
import com.toursafe.data.repository.combineStatus
import com.toursafe.util.LiveMarkers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LiveMarkersTest {

    private fun place(id: String, shelter: Boolean, km: Double, lat: Double, lng: Double, demo: Boolean = false) =
        SafeAccommodation(
            id = id, name = "P$id", type = "Hospital · OpenStreetMap", address = "addr",
            distanceKm = km, isEmergencyShelter = shelter, latitude = lat, longitude = lng,
            isDemo = demo, isVerified = false
        )

    private fun alert(id: String, km: Double, lat: Double, lng: Double) = DisasterAlert(
        id = id, title = "A$id", disasterType = "Earthquake", severity = AlertSeverity.WARNING,
        affectedArea = "x", distanceKm = km, timestamp = "now", sourceLabel = "USGS",
        description = "d", officialInstructions = emptyList(), recommendedActions = emptyList(),
        latitude = lat, longitude = lng, isSimulated = false, isVerified = true
    )

    @Test
    fun noData_returnsNoMarkers_notEvenUser() {
        assertTrue(LiveMarkers.build(7.88, 98.39, "You", emptyList(), emptyList()).isEmpty())
    }

    @Test
    fun placesAndAlerts_becomeMarkers_withTypesAndFlagsPreserved() {
        val markers = LiveMarkers.build(
            7.88, 98.39, "You",
            places = listOf(
                place("1", shelter = false, km = 1.0, lat = 7.889, lng = 98.39),
                place("2", shelter = true, km = 2.0, lat = 7.88, lng = 98.40)
            ),
            alerts = listOf(alert("9", km = 2.0, lat = 7.90, lng = 98.39))
        )
        assertEquals(4, markers.size)
        assertEquals(MarkerType.USER_LOCATION, markers[0].type)
        assertEquals(MarkerType.EMERGENCY_SERVICE, markers[1].type)
        assertEquals(MarkerType.EMERGENCY_SHELTER, markers[2].type)
        assertEquals(MarkerType.HAZARD_ZONE, markers[3].type)
        assertTrue(markers.drop(1).take(2).all { !it.isDemo && !it.isVerified })
        assertTrue(markers[3].isVerified && !markers[3].isDemo)
        assertTrue(markers.all { it.normX in 0f..1f && it.normY in 0f..1f })
    }

    @Test
    fun farAwayAlert_isLeftOffTheMap_insteadOfBeingClampedToTheEdge() {
        val markers = LiveMarkers.build(
            7.88, 98.39, "You",
            places = listOf(place("1", false, 1.0, 7.889, 98.39)),
            alerts = listOf(alert("far", km = 600.0, lat = 12.0, lng = 100.0))
        )
        assertFalse(markers.any { it.id == "alert-far" })
    }

    @Test
    fun demoPlaces_staySafeAccommodation_andDemoFlagKept() {
        val markers = LiveMarkers.build(
            7.88, 98.39, "You",
            places = listOf(place("d", shelter = false, km = 1.0, lat = 7.889, lng = 98.39, demo = true)),
            alerts = emptyList()
        )
        assertEquals(MarkerType.SAFE_ACCOMMODATION, markers[1].type)
        assertTrue(markers[1].isDemo)
    }

    @Test
    fun combineStatus_neverClaimsResultBeforeBothAnswered() {
        assertEquals(LoadStatus.LOADING, combineStatus(LoadStatus.LOADING, LoadStatus.LOADED))
        assertEquals(LoadStatus.IDLE, combineStatus(LoadStatus.IDLE, LoadStatus.LOADED))
        assertEquals(LoadStatus.UNAVAILABLE, combineStatus(LoadStatus.UNAVAILABLE, LoadStatus.UNAVAILABLE))
        assertEquals(LoadStatus.PARTIAL, combineStatus(LoadStatus.UNAVAILABLE, LoadStatus.LOADED))
        assertEquals(LoadStatus.PARTIAL, combineStatus(LoadStatus.PARTIAL, LoadStatus.LOADED))
        assertEquals(LoadStatus.LOADED, combineStatus(LoadStatus.LOADED, LoadStatus.LOADED))
    }
}
