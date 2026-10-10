package com.toursafe

import com.toursafe.data.models.AlertSeverity
import com.toursafe.data.repository.DataSourceState
import com.toursafe.data.repository.LiveSafetyRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LiveRepositoryTest {

    @Test
    fun getAlerts_combinesSourcesAndSortsBySeverityThenDistance() = runBlocking {
        val usgsJson = """{
            "type": "FeatureCollection",
            "features": [{
                "type": "Feature",
                "id": "1",
                "properties": { "mag": 6.5, "place": "30 km from User", "time": 1700000000000, "alert": "yellow" },
                "geometry": { "type": "Point", "coordinates": [98.5, 7.9, 10.0] }
            }]
        }"""

        val gdacsJson = """{
            "type": "FeatureCollection",
            "features": [{
                "type": "Feature",
                "properties": { "eventtype": "TC", "eventid": "100", "iscurrent": "true", "alertlevel": "Red", "name": "Cyclone Alpha", "country": "Testland" },
                "geometry": { "type": "Point", "coordinates": [98.4, 7.85] }
            }]
        }"""

        val repo = LiveSafetyRepository(
            httpGet = { url ->
                when {
                    url.contains("earthquake.usgs.gov") -> usgsJson
                    url.contains("gdacs.org") -> gdacsJson
                    else -> error("unexpected GET $url")
                }
            }
        )

        val state = repo.getAlerts(7.8804, 98.3923, demoMode = false)
        assertTrue(state is DataSourceState.Success)
        val alerts = (state as DataSourceState.Success).data
        assertEquals(2, alerts.size)
        // Red GDACS cyclone (CRITICAL) comes before yellow USGS earthquake (WARNING)
        assertEquals(AlertSeverity.CRITICAL, alerts[0].severity)
        assertEquals(AlertSeverity.WARNING, alerts[1].severity)
    }

    @Test
    fun getAlerts_returnsNoVerifiedSource_whenZeroCoordinates() = runBlocking {
        val repo = LiveSafetyRepository()
        val state = repo.getAlerts(0.0, 0.0, demoMode = false)
        assertTrue(state is DataSourceState.NoVerifiedSource)
    }

    private val usgsOk = """{"type":"FeatureCollection","features":[{"type":"Feature","id":"1",
        "properties":{"mag":5.2,"place":"20 km from User","time":1700000000000},
        "geometry":{"type":"Point","coordinates":[98.5,7.9,10.0]}}]}"""

    private val overpassOk = """{"elements":[{"type":"node","id":42,"lat":7.881,"lon":98.392,
        "tags":{"amenity":"hospital","name":"Test Hospital","emergency":"yes"}}]}"""

    @Test
    fun getAlerts_isUnavailable_whenEverySourceFails() = runBlocking {
        val repo = LiveSafetyRepository(httpGet = { throw java.io.IOException("offline") })
        val state = repo.getAlerts(7.8804, 98.3923, demoMode = false)
        assertTrue(state is DataSourceState.Unavailable)
    }

    @Test
    fun getAlerts_isPartial_whenOneSourceFails() = runBlocking {
        val repo = LiveSafetyRepository(
            httpGet = { url ->
                if (url.contains("earthquake.usgs.gov")) usgsOk else throw java.io.IOException("gdacs down")
            }
        )
        val state = repo.getAlerts(7.8804, 98.3923, demoMode = false)
        assertTrue(state is DataSourceState.Success)
        state as DataSourceState.Success
        assertTrue(state.isPartial)
        assertEquals(1, state.data.size)
    }

    @Test
    fun getAlerts_emptySuccess_isNotPartial_whenNothingNearby() = runBlocking {
        val empty = """{"type":"FeatureCollection","features":[]}"""
        val repo = LiveSafetyRepository(httpGet = { empty })
        val state = repo.getAlerts(7.8804, 98.3923, demoMode = false)
        assertTrue(state is DataSourceState.Success)
        state as DataSourceState.Success
        assertTrue(state.data.isEmpty())
        assertTrue(!state.isPartial)
    }

    @Test
    fun getAlerts_usesCache_secondCallDoesNotHitNetwork() = runBlocking {
        var calls = 0
        val repo = LiveSafetyRepository(httpGet = { calls++; usgsOk })
        repo.getAlerts(7.8804, 98.3923, demoMode = false)
        val afterFirst = calls
        repo.getAlerts(7.8804, 98.3923, demoMode = false)
        assertEquals(afterFirst, calls)
    }

    @Test
    fun getAlerts_demoMode_neverTouchesNetwork() = runBlocking {
        val repo = LiveSafetyRepository(httpGet = { error("must not be called in demo mode") })
        val state = repo.getAlerts(7.8804, 98.3923, demoMode = true)
        assertTrue(state is DataSourceState.Success)
        assertTrue((state as DataSourceState.Success).data.all { it.isSimulated })
    }

    @Test
    fun getAccommodations_parsesOverpass_andMarksUnverified() = runBlocking {
        val repo = LiveSafetyRepository(httpPostForm = { _, _ -> overpassOk })
        val state = repo.getAccommodations(7.8804, 98.3923, demoMode = false)
        assertTrue(state is DataSourceState.Success)
        val places = (state as DataSourceState.Success).data
        assertEquals(1, places.size)
        assertTrue(!places[0].isDemo)
        assertTrue(!places[0].isVerified)
    }

    @Test
    fun getAccommodations_fallsBackToSecondEndpoint() = runBlocking {
        val tried = mutableListOf<String>()
        val repo = LiveSafetyRepository(
            httpPostForm = { url, _ ->
                tried += url
                if (tried.size == 1) throw java.io.IOException("first mirror down") else overpassOk
            }
        )
        val state = repo.getAccommodations(7.8804, 98.3923, demoMode = false)
        assertTrue(state is DataSourceState.Success)
        assertEquals(2, tried.size)
    }

    @Test
    fun getAccommodations_isUnavailable_whenAllEndpointsFail() = runBlocking {
        val repo = LiveSafetyRepository(httpPostForm = { _, _ -> throw java.io.IOException("offline") })
        val state = repo.getAccommodations(7.8804, 98.3923, demoMode = false)
        assertTrue(state is DataSourceState.Unavailable)
    }
}
