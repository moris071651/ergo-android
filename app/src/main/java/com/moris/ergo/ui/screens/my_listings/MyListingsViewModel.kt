package com.moris.ergo.ui.screens.my_listings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moris.ergo.data.api.CreateListingRequestDTO
import com.moris.ergo.data.api.ListingResponseDTO
import com.moris.ergo.data.api.ToggleListingActiveDTO
import com.moris.ergo.data.repository.ListingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class MyListingsViewModel @Inject constructor(
    private val repository: ListingRepository
) : ViewModel() {

    private val _listings = MutableStateFlow<List<ListingResponseDTO>>(emptyList())
    val listings: StateFlow<List<ListingResponseDTO>> = _listings

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadListings() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _listings.value = repository.getMyListings()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteListing(id: String) {
        viewModelScope.launch {
            try {
                repository.deleteListing(id)
                _listings.value = _listings.value.filterNot { it.id == id }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun toggleListingActive(
        listingId: String,
        currentState: Boolean
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = ToggleListingActiveDTO(isActive = !currentState)
                val listing = repository.toggleListingActive(listingId, request)
                _listings.value = _listings.value.map {
                    if (it.id == listing.id) listing else it
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createListing(
        request: CreateListingRequestDTO,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.createListing(request)
                loadListings()
                onSuccess()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}
