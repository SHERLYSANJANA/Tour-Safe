package com.toursafe.data.models

data class EmergencyContact(
    val id: String,
    val name: String,
    val relationship: String,
    val phoneNumber: String,
    val isPrimary: Boolean = false
)
