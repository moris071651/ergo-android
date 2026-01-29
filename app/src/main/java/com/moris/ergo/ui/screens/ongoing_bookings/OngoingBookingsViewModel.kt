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

//@Serializable
//data class CreateBookingRequestDTO(
//    @SerialName("start_at") val startAt: String,
//    @SerialName("end_at") val endAt: String,
//    @SerialName("address_id") val addressId: String
//)
//
//@Serializable
//data class CreateBookingResponseDTO(
//    val id: String
//)
//
//@Serializable
//data class BookingResponseDTO(
//    val id: String,
//    val state: BookingState,
//    @SerialName("start_at") val startAt: String,
//    @SerialName("end_at") val endAt: String,
//    @SerialName("created_at") val createdAt: String,
//    val address: AddressDTO,
//    val listing: ListingResponsePublicDTO,
//    val reason: String? = null,
//    @SerialName("worker_id") val workerId: String? = null,
//    @SerialName("customer_id") val customerId: String? = null,
//    @SerialName("client_secret") val clientSecret: String? = null
//)
//
//@Serializable
//enum class BookingState {
//    PENDING,
//    ACCEPTED,
//    REJECTED,
//    CANCELLED,
//    COMPLETED
//}
//
////class BookingState(str, Enum):
////    ON_HOLD = "on-hold"
////    CREATED = "created"
////    WAITING_APPROVAL = "waiting-approval"
////    PENDING_PAYMENT = "pending-payment"
////    PENDING = "pending"
////    IN_PROGRESS = "in-progress"
////    CANCELED = "canceled"
////    FINISHED = "finished"
////    FINISH_PENDING = "finish-pending"


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
                _customerBookings.value = repository.getCurrentUserBookings() // returns null if 401 and 404
                _workerBookings.value = repository.getCurrentWorkerBookings() // returns null if 401 and 404
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
