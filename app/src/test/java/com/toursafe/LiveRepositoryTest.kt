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
}
