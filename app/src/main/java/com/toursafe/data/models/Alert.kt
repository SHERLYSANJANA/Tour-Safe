package com.toursafe.data.models

enum class AlertSeverity {
    CRITICAL,
    WARNING,
    ADVISORY,
    INFO
}

data class DisasterAlert(
    val id: String,
    val title: String,
    val disasterType: String,
    val severity: AlertSeverity,
    val affectedArea: String,
    val distanceKm: Double,
    val timestamp: String,
    val sourceLabel: String,
    val description: String,
    val officialInstructions: List<String>,
    val recommendedActions: List<String>,
    val latitude: Double = 7.8920,
    val longitude: Double = 98.2980,
    val isSimulated: Boolean = true
)
