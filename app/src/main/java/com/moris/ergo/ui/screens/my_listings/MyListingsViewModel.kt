package com.moris.ergo.ui.screens.my_listings

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moris.ergo.data.dto.CreateListingRequestDTO
import com.moris.ergo.data.dto.EditListingRequestDTO
import com.moris.ergo.data.dto.ToggleListingActiveDTO
import com.moris.ergo.data.repository.ListingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class MyListingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: ListingRepository
) : ViewModel() {

    private val _listings = MutableStateFlow<List<MyListingItem>>(emptyList())
    val listings: StateFlow<List<MyListingItem>> = _listings

    private val _editableListing = MutableStateFlow<MyListingItem?>(null)
    val editableListing: StateFlow<MyListingItem?> = _editableListing

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadListings() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _listings.value = repository.getMyListings().map { listing ->
                    listing.toMyListingItemUiState().apply {
                        imageUrls = repository.getListingImagesById(listing.id).map {
                            it.url
                        }
                    }
                }
            }
            catch (e: Exception) {
                _error.value = e.message
            }
            finally {
                _isLoading.value = false
            }
        }
    }

    fun loadEditableListing(listingId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _editableListing.value =
                    repository.getListingDetail3(listingId).toMyListingItemUiState()
            }
            catch (e: Exception) {
                _error.value = e.message
            }
            finally {
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
                _listings.value = _listings.value.map { it ->
                    if (it.id == listing.id) {
                        listing.toMyListingItemUiState().apply {
                            imageUrls = repository.getListingImagesById(listing.id).map { image ->
                                image.url
                            }
                        }
                    } else it
                }
            }
            catch (e: Exception) {
                _error.value = e.message
            }
            finally {
                _isLoading.value = false
            }
        }
    }

    fun editListing(
        listingId: String,
        request: EditListingRequestDTO,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.editListing(listingId, request)
                loadListings()
                onSuccess()
            }
            catch (e: Exception) {
                _error.value = e.message
            }
            finally {
                _isLoading.value = false
            }
        }
    }

    fun createListingWithImages(request: CreateListingRequestDTO, uris: List<Uri>, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val imageBytes = uris.map{ uri ->
                    context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                }

                val r = repository.createListing(request)
                repository.uploadListingImagesById(r.id, imageBytes.filterNotNull())

                loadListings()
                onSuccess()
            }
            catch (e: Exception) {
                _error.value = e.message
            }
            finally {
                _isLoading.value = false
            }
        }

    }

}
