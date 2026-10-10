package com.toursafe.data.models

data class PropertyReview(
    val id: String,
    val accommodationId: String,
    val userName: String,
    val userCountry: String,
    val rating: Double,
    val date: String,
    val comment: String,
    val safetyVerified: Boolean = true
)
