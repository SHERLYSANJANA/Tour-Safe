package com.toursafe

import com.toursafe.data.repository.DataSourceState
import com.toursafe.data.repository.MockSafetyRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DemoModeTest {

    private val repository = MockSafetyRepository()

    @Test
    fun repository_returnsNoVerifiedSource_whenDemoModeIsOff() {
        val alertState = repository.getAlerts(0.0, 0.0, demoMode = false)
        val accState = repository.getAccommodations(0.0, 0.0, demoMode = false)

        assertTrue(alertState is DataSourceState.NoVerifiedSource)
        assertTrue(accState is DataSourceState.NoVerifiedSource)
    }

    @Test
    fun repository_returnsDemoData_whenDemoModeIsOn() {
        val alertState = repository.getAlerts(0.0, 0.0, demoMode = true)
        val accState = repository.getAccommodations(0.0, 0.0, demoMode = true)

        assertTrue(alertState is DataSourceState.Success)
        assertTrue(accState is DataSourceState.Success)

        val alerts = (alertState as DataSourceState.Success).data
        val accs = (accState as DataSourceState.Success).data

        assertEquals(2, alerts.size)
        assertEquals(2, accs.size)
        assertTrue(accs.all { it.isDemo })
        assertTrue(alerts.all { it.isSimulated })
    }
}
