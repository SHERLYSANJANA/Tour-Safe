package com.toursafe

import com.toursafe.util.GeoMath
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GeoAndMapTest {

    @Test
    fun projectToCanvas_placesUserAtCenter() {
        val (x, y) = GeoMath.projectToCanvas(7.88, 98.39, 7.88, 98.39, 10.0)
        assertEquals(0.5f, x, 0.001f)
        assertEquals(0.5f, y, 0.001f)
    }

    @Test
    fun projectToCanvas_clampsExtremeCoordinates() {
        val (x, y) = GeoMath.projectToCanvas(0.0, 0.0, 80.0, 180.0, 10.0)
        assertTrue(x in 0.05f..0.95f)
        assertTrue(y in 0.05f..0.95f)
    }
}
