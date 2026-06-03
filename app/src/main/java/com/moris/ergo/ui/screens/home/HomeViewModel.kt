package com.moris.ergo.ui.screens.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moris.ergo.data.repository.ListingRepository
import com.moris.ergo.data.repository.WorkerRepository
import com.moris.ergo.data.scheme.Category
import com.moris.ergo.data.scheme.ListingBriefInfo
import com.moris.ergo.data.scheme.WorkerBriefInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel@Inject constructor(
    private val listingRepository: ListingRepository,
    private val workerRepository: WorkerRepository
) : ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _popularWorkers = MutableStateFlow<List<WorkerBriefInfo>>(emptyList())
    val popularWorkers: StateFlow<List<WorkerBriefInfo>> = _popularWorkers

    private val _popularListings = MutableStateFlow<List<ListingBriefInfo>>(emptyList())
    val popularListings: StateFlow<List<ListingBriefInfo>> = _popularListings

    init {
        _isLoading.value = true

        loadPopularWorkers()
        loadPopularListings()

        _isLoading.value = false
    }
    private fun loadPopularWorkers() {
        _popularWorkers.value = listOf()

        viewModelScope.launch {
            _isLoading.value = true
            try {
                _popularWorkers.value = workerRepository.getPopularWorkerByCity(limit = 10, city = "sofia")
            }
            catch (e: Exception) {
                Log.e("HomeViewModel", "Failed to load workers", e)
            }
            finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadPopularListings() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val listings = listingRepository.getPopularListings(limit = 10, city = "sofia")

                val updatedListings = listings.map { listing ->
                    val primaryImage = listingRepository.getListingPrimaryImagesById(listing.id)

                    listing.apply {
                        primaryImageUrl = primaryImage?.url ?: ""
                    }
                }

                _popularListings.value = updatedListings
            }
            catch (e: Exception) {
                Log.e("HomeViewModel", "Failed to load listings", e)
            }
            finally {
                _isLoading.value = false
            }
        }
    }
}
