package com.toursafe.viewmodels

import androidx.lifecycle.ViewModel
import com.toursafe.data.mock.MockData
import com.toursafe.data.models.PropertyReview
import com.toursafe.data.models.SafeAccommodation
import com.toursafe.util.LocationUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AccommodationViewModel : ViewModel() {

    private val _accommodations = MutableStateFlow(MockData.accommodations)
    val accommodations: StateFlow<List<SafeAccommodation>> = _accommodations.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _onlyShelters = MutableStateFlow(false)
    val onlyShelters: StateFlow<Boolean> = _onlyShelters.asStateFlow()

    private val _reviews = MutableStateFlow(MockData.propertyReviews)
    val reviews: StateFlow<List<PropertyReview>> = _reviews.asStateFlow()

    fun updateGpsLocation(userLat: Double, userLng: Double) {
        if (userLat == 0.0 && userLng == 0.0) return
        val updated = MockData.accommodations.map { acc ->
            val dist = LocationUtils.calculateDistanceKm(userLat, userLng, acc.latitude, acc.longitude)
            acc.copy(distanceKm = dist)
        }.sortedBy { it.distanceKm }
        _accommodations.value = updated
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleShelterFilter(onlyShelters: Boolean) {
        _onlyShelters.value = onlyShelters
    }

    fun getFilteredAccommodations(): List<SafeAccommodation> {
        return _accommodations.value.filter { acc ->
            val matchesQuery = acc.name.contains(_searchQuery.value, ignoreCase = true) ||
                    acc.address.contains(_searchQuery.value, ignoreCase = true) ||
                    acc.type.contains(_searchQuery.value, ignoreCase = true)
            val matchesShelter = !_onlyShelters.value || acc.isEmergencyShelter
            matchesQuery && matchesShelter
        }
    }

    fun getAccommodationById(id: String): SafeAccommodation? {
        return _accommodations.value.find { it.id == id }
    }

    fun addReview(accommodationId: String, userName: String, rating: Double, comment: String) {
        val newRev = PropertyReview(
            id = "rev-${System.currentTimeMillis()}",
            accommodationId = accommodationId,
            userName = userName,
            userCountry = "Verified Traveler",
            rating = rating,
            date = "Just now",
            comment = comment
        )
        _reviews.value = listOf(newRev) + _reviews.value
    }
}
