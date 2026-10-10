package com.toursafe

import com.toursafe.data.models.AlertSeverity
import com.toursafe.data.remote.GdacsParser
import com.toursafe.data.remote.OverpassParser
import com.toursafe.data.remote.UsgsParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RemoteParsersTest {

    @Test
    fun usgsParser_mapsSeverities_andFiltersByDistance() {
        val json = """{
            "type": "FeatureCollection",
            "features": [
                {
                    "type": "Feature",
                    "id": "eq1",
                    "properties": { "mag": 7.5, "place": "70 km W of Phuket", "time": 1700000000000, "alert": "red", "tsunami": 1, "url": "https://eq.example" },
                    "geometry": { "type": "Point", "coordinates": [98.0, 7.8, 15.0] }
                },
                {
                    "type": "Feature",
                    "id": "eq2",
                    "properties": { "mag": 4.8, "place": "Far Away", "time": 1700000000000 },
                    "geometry": { "type": "Point", "coordinates": [0.0, 0.0, 5.0] }
                }
            ]
        }"""

        val parsed = UsgsParser.parse(json, userLat = 7.8804, userLng = 98.3923, radiusKm = 500.0)
        assertEquals(1, parsed.size)
        val alert = parsed[0]
        assertEquals(AlertSeverity.CRITICAL, alert.severity)
        assertTrue(alert.isVerified)
        assertFalse(alert.isSimulated)
        assertTrue(alert.description.contains("tsunami"))
    }

    @Test
    fun overpassParser_parsesAmenities_andMarksUnverified() {
        val json = """{
            "elements": [
                {
                    "type": "node",
                    "id": 1001,
                    "lat": 7.89,
                    "lon": 98.40,
                    "tags": { "amenity": "hospital", "name": "Phuket General Hospital", "emergency": "yes" }
                },
                {
                    "type": "node",
                    "id": 1002,
                    "lat": 7.88,
                    "lon": 98.38,
                    "tags": { "social_facility": "shelter", "name": "City Shelter" }
                }
            ]
        }"""

        val parsed = OverpassParser.parse(json, userLat = 7.8804, userLng = 98.3923)
        assertEquals(2, parsed.size)
        assertTrue(parsed.all { !it.isVerified })
        assertTrue(parsed.all { !it.isDemo })
        assertEquals("Hospital", parsed.find { it.id.contains("1001") }?.type?.substringBefore(" "))
    }
}
