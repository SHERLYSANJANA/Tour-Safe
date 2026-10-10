package com.toursafe.data.repository

import com.toursafe.data.mock.MockData
import com.toursafe.data.models.DisasterAlert
import com.toursafe.data.models.SafeAccommodation
import com.toursafe.data.models.UserSafetyStatus

sealed class DataSourceState<out T> {
    object NoVerifiedSource : DataSourceState<Nothing>()
    data class Success<T>(val data: List<T>) : DataSourceState<T>()
}

interface SafetyRepository {
    fun getUserStatus(): UserSafetyStatus
    fun getAlerts(userLat: Double = 0.0, userLng: Double = 0.0, demoMode: Boolean = false): DataSourceState<DisasterAlert>
    fun getAccommodations(userLat: Double = 0.0, userLng: Double = 0.0, demoMode: Boolean = false): DataSourceState<SafeAccommodation>
}

class MockSafetyRepository : SafetyRepository {
    override fun getUserStatus(): UserSafetyStatus = UserSafetyStatus()

    override fun getAlerts(userLat: Double, userLng: Double, demoMode: Boolean): DataSourceState<DisasterAlert> {
        return if (demoMode) {
            DataSourceState.Success(MockData.demoAlerts)
        } else {
            DataSourceState.NoVerifiedSource
        }
    }

    override fun getAccommodations(userLat: Double, userLng: Double, demoMode: Boolean): DataSourceState<SafeAccommodation> {
        return if (demoMode) {
            DataSourceState.Success(MockData.demoAccommodations)
        } else {
            DataSourceState.NoVerifiedSource
        }
    }
}
