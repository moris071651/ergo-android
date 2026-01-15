package com.moris.ergo.ui.screens.booking_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moris.ergo.data.api.BookingResponseDTO
import com.moris.ergo.data.api.CurrentUserResponseDTO
import com.moris.ergo.data.repository.BookingRepository
import com.moris.ergo.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class BookingDetailViewModel @Inject constructor(
    private val repository: BookingRepository,
    private val userRepo: UserRepository
) : ViewModel() {
    private val _user = MutableStateFlow<CurrentUserResponseDTO?>(null)
    val user: StateFlow<CurrentUserResponseDTO?> = _user

    private val _booking = MutableStateFlow<BookingResponseDTO?>(null)
    val booking = _booking.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _isWorker = MutableStateFlow<Boolean?>(null)
    val isWorker = _isWorker.asStateFlow()

    fun loadBooking(bookingId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _booking.value = repository.getBookingByIdCustomer(bookingId)
                _user.value = userRepo.getCurrentUser()
                _isWorker.value = _user.value!!.id  == _booking.value!!.workerId
            }
            catch (e: Exception) {
                _error.value = e.message
            }
            finally {
                _isLoading.value = false
            }
        }
    }

    fun acceptBooking(id: String) = action { repository.acceptBooking(id) }
    fun rejectBooking(id: String) = action { repository.rejectBooking(id) }
    fun startBooking(id: String) = action { repository.startBooking(id) }
    fun markFinishPending(id: String) = action { repository.markFinishPending(id) }

    fun confirmFinished(id: String) = action { repository.confirmFinished(id) }
    fun denyFinish(id: String) = action { repository.denyFinish(id) }

    fun setError(message: String?) {
        _error.value = message
    }

    private fun action(callback: suspend () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                callback()
                _booking.value = _booking.value?.id?.let {
                    repository.getBookingByIdCustomer(it)
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
}
