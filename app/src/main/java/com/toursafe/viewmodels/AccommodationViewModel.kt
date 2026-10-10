package com.toursafe.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toursafe.data.mock.MockData
import com.toursafe.data.models.PropertyReview
import com.toursafe.data.models.SafeAccommodation
import com.toursafe.data.repository.LoadStatus
import com.toursafe.data.repository.RepositoryProvider
import com.toursafe.data.repository.SafetyRepository
import com.toursafe.data.repository.items
import com.toursafe.data.repository.toStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AccommodationViewModel(
    private val repository: SafetyRepository = RepositoryProvider.live
) : ViewModel() {

    private val _accommodations = MutableStateFlow<List<SafeAccommodation>>(emptyList())
    val accommodations: StateFlow<List<SafeAccommodation>> = _accommodations.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _onlyShelters = MutableStateFlow(false)
    val onlyShelters: StateFlow<Boolean> = _onlyShelters.asStateFlow()

    /** Observable version of [getFilteredAccommodations]: recomputes when data, search or filter change. */
    val filteredAccommodations: StateFlow<List<SafeAccommodation>> =
        combine(_accommodations, _searchQuery, _onlyShelters) { list, query, onlyShelters ->
            list.filter { acc ->
                val matchesQuery = acc.name.contains(query, ignoreCase = true) ||
                        acc.address.contains(query, ignoreCase = true) ||
                        acc.type.contains(query, ignoreCase = true)
                matchesQuery && (!onlyShelters || acc.isEmergencyShelter)
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _reviews = MutableStateFlow(MockData.propertyReviews)
    val reviews: StateFlow<List<PropertyReview>> = _reviews.asStateFlow()

    private val _status = MutableStateFlow(LoadStatus.IDLE)
    val status: StateFlow<LoadStatus> = _status.asStateFlow()

    private var refreshJob: Job? = null

    fun refreshData(userLat: Double = 0.0, userLng: Double = 0.0, demoMode: Boolean = false) {
        refreshJob?.cancel()
        _status.value = if (demoMode || userLat != 0.0 || userLng != 0.0) LoadStatus.LOADING else LoadStatus.IDLE
        refreshJob = viewModelScope.launch {
            val state = repository.getAccommodations(userLat, userLng, demoMode)
            _accommodations.value = state.items()
            _status.value = state.toStatus()
        }
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
        return _accommodations.value.find { it.id == id } ?: MockData.demoAccommodations.find { it.id == id }
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
