package com.toursafe.data.repository

import com.toursafe.data.mock.MockData
import com.toursafe.data.models.DisasterAlert
import com.toursafe.data.models.SafeAccommodation
import com.toursafe.data.models.UserSafetyStatus

sealed class DataSourceState<out T> {
    /** No usable source for this request (e.g. no GPS fix yet, or demo mode off for a mock-only repository). */
    object NoVerifiedSource : DataSourceState<Nothing>()

    /** Every data source we tried failed (offline, timeout, server error). Distinct from "nothing nearby". */
    object Unavailable : DataSourceState<Nothing>()

    /**
     * Sources answered. [data] may be empty, which means "checked, nothing found nearby".
     * [isPartial] is true when at least one source failed, so the list may be incomplete.
     */
    data class Success<T>(val data: List<T>, val isPartial: Boolean = false) : DataSourceState<T>()
}

interface SafetyRepository {
    fun getUserStatus(): UserSafetyStatus
    suspend fun getAlerts(userLat: Double = 0.0, userLng: Double = 0.0, demoMode: Boolean = false): DataSourceState<DisasterAlert>
    suspend fun getAccommodations(userLat: Double = 0.0, userLng: Double = 0.0, demoMode: Boolean = false): DataSourceState<SafeAccommodation>
}

class MockSafetyRepository : SafetyRepository {
    override fun getUserStatus(): UserSafetyStatus = UserSafetyStatus()

    override suspend fun getAlerts(userLat: Double, userLng: Double, demoMode: Boolean): DataSourceState<DisasterAlert> {
        return if (demoMode) {
            DataSourceState.Success(MockData.demoAlerts)
        } else {
            DataSourceState.NoVerifiedSource
        }
    }

    override suspend fun getAccommodations(userLat: Double, userLng: Double, demoMode: Boolean): DataSourceState<SafeAccommodation> {
        return if (demoMode) {
            DataSourceState.Success(MockData.demoAccommodations)
        } else {
            DataSourceState.NoVerifiedSource
        }
    }
}

/** One shared live repository so Home, Alerts, Safe Places and Map reuse the same in-memory cache. */
object RepositoryProvider {
    val live: SafetyRepository by lazy { LiveSafetyRepository() }
}
