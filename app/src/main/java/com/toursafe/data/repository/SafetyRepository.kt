package com.toursafe.data.repository

import com.toursafe.data.mock.MockData
import com.toursafe.data.models.DisasterAlert
import com.toursafe.data.models.SafeAccommodation
import com.toursafe.data.models.UserSafetyStatus

interface SafetyRepository {
    fun getUserStatus(): UserSafetyStatus
    fun getAlerts(userLat: Double = 0.0, userLng: Double = 0.0): List<DisasterAlert>
    fun getAccommodations(userLat: Double = 0.0, userLng: Double = 0.0): List<SafeAccommodation>
}

class MockSafetyRepository : SafetyRepository {
    override fun getUserStatus(): UserSafetyStatus = UserSafetyStatus()

    override fun getAlerts(userLat: Double, userLng: Double): List<DisasterAlert> {
        return MockData.getAnchoredAlerts(userLat, userLng)
    }

    override fun getAccommodations(userLat: Double, userLng: Double): List<SafeAccommodation> {
        return MockData.getAnchoredAccommodations(userLat, userLng)
    }
}
