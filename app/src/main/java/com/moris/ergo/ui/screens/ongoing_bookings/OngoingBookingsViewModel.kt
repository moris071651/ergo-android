package com.moris.ergo.ui.screens.ongoing_bookings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moris.ergo.data.dto.BookingResponseDTO
import com.moris.ergo.data.repository.BookingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class OngoingBookingsViewModel @Inject constructor(
    private val repository: BookingRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _customerBookings = MutableStateFlow<List<BookingResponseDTO>?>(null)
    val customerBookings = _customerBookings.asStateFlow()

    private val _workerBookings = MutableStateFlow<List<BookingResponseDTO>?>(null)
    val workerBookings = _workerBookings.asStateFlow()

    private val _customerUnauthorized = MutableStateFlow(false)
    val customerUnauthorized = _customerUnauthorized.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadBookings() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _customerBookings.value = repository.getCurrentUserBookings()
                _workerBookings.value = repository.getCurrentWorkerBookings()
            }
            catch (e: Exception) {
                _customerBookings.value = null
                _workerBookings.value = null
                _error.value = e.toString()
            }
            finally {
                _customerUnauthorized.value = (_error.value == null && _customerBookings.value == null && _workerBookings.value == null)
                _isLoading.value = false
            }
        }
    }
}
