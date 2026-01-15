package com.moris.ergo.ui.screens.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moris.ergo.data.api.AddressDTO
import com.moris.ergo.data.api.AddressRepository
import com.moris.ergo.data.api.CreateBookingRequestDTO
import com.moris.ergo.data.repository.BookingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val bookingRepository: BookingRepository,
    private val addressRepository: AddressRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _addresses = MutableStateFlow<List<AddressDTO>>(emptyList())
    val addresses = _addresses.asStateFlow()

    fun loadAddresses() {
        viewModelScope.launch {
            try {
                _addresses.value = addressRepository.getAll()
            }
            catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun book(
        listingId: String,
        startAt: String,
        endAt: String,
        addressId: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                bookingRepository.bookListing(
                    listingId,
                    CreateBookingRequestDTO(
                        startAt = startAt,
                        endAt = endAt,
                        addressId = addressId
                    )
                )
                onSuccess()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}
