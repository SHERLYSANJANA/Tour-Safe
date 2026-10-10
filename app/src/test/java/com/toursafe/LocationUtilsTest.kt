package com.toursafe

import com.toursafe.util.LocationUtils
import org.junit.Assert.assertEquals
import org.junit.Test

class LocationUtilsTest {

    @Test
    fun calculateDistanceKm_returnsZero_whenZeroCoordinates() {
        val dist = LocationUtils.calculateDistanceKm(0.0, 0.0, 7.8804, 98.3923)
        assertEquals(0.0, dist, 0.001)
    }
}
