package com.toursafe.data.repository

import com.toursafe.data.mock.MockData
import com.toursafe.data.models.DisasterAlert
import com.toursafe.data.models.UserSafetyStatus

interface SafetyRepository {
    fun getUserStatus(): UserSafetyStatus
    fun getAlerts(): List<DisasterAlert>
}

class MockSafetyRepository : SafetyRepository {
    override fun getUserStatus(): UserSafetyStatus = UserSafetyStatus()
    override fun getAlerts(): List<DisasterAlert> = MockData.alerts
}
