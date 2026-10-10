package com.toursafe.data.models

enum class SafetyLevel {
    SAFE,
    MOVING_TO_SAFETY,
    NEED_HELP,
    CRITICAL_WARNING
}

data class UserSafetyStatus(
    val level: SafetyLevel = SafetyLevel.SAFE,
    val lastUpdatedText: String = "Just now",
    val locationName: String = "Phuket Old Town, Thailand",
    val coordinates: String = "7.8804° N, 98.3923° E",
    val statusMessage: String = "I am safe and currently at secure accommodation."
)
