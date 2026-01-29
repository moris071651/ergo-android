package com.moris.ergo.data.repository

import android.util.Log
import com.moris.ergo.data.dto.BookingResponseDTO
import com.moris.ergo.data.dto.CreateBookingRequestDTO
import com.moris.ergo.data.dto.CreateBookingResponseDTO
import com.moris.ergo.data.api.ErgoServerApi
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.HttpStatusCode
import jakarta.inject.Inject

interface BookingRepository {
    suspend fun bookListing(listingId: String, body: CreateBookingRequestDTO): CreateBookingResponseDTO
    suspend fun getCurrentUserBookings(): List<BookingResponseDTO>?
    suspend fun getCurrentWorkerBookings(): List<BookingResponseDTO>?
    suspend fun getBookingByIdCustomer(bookingId: String): BookingResponseDTO
    suspend fun getBookingByIdWorker(bookingId: String): BookingResponseDTO
    suspend fun acceptBooking(bookingId: String): BookingResponseDTO
    suspend fun rejectBooking(bookingId: String): BookingResponseDTO
    suspend fun startBooking(bookingId: String): BookingResponseDTO
    suspend fun markFinishPending(bookingId: String): BookingResponseDTO
    suspend fun confirmFinished(bookingId: String): BookingResponseDTO
    suspend fun denyFinish(bookingId: String): BookingResponseDTO
}

private suspend fun <T> helper(callback: suspend () -> T): T? {
    return try {
        callback()
    }
    catch (e: ClientRequestException) {
        when (e.response.status) {
            HttpStatusCode.Unauthorized -> {
                null
            }
            HttpStatusCode.NotFound -> {
                null
            }
            else -> {
                throw e
            }
        }
    }
    catch (e: Exception) {
        Log.e("API", "Network error", e)
        throw e
    }
}

class BookingRepositoryImpl @Inject constructor(
    private val api: ErgoServerApi
) : BookingRepository {
    override suspend fun bookListing(listingId: String, body: CreateBookingRequestDTO): CreateBookingResponseDTO =
        api.bookListing(listingId, body)

    override suspend fun getCurrentUserBookings(): List<BookingResponseDTO>? = helper {
        api.getCurrentUserBookings()
    }

    override suspend fun getCurrentWorkerBookings(): List<BookingResponseDTO>? = helper {
        api.getCurrentWorkerBookings()
    }

    override suspend fun getBookingByIdCustomer(bookingId: String): BookingResponseDTO =
        api.getBookingByIdCustomer(bookingId)

    override suspend fun getBookingByIdWorker(bookingId: String): BookingResponseDTO =
        api.getBookingByIdWorker(bookingId)

    override suspend fun acceptBooking(bookingId: String): BookingResponseDTO =
        api.acceptBooking(bookingId)

    override suspend fun rejectBooking(bookingId: String): BookingResponseDTO =
        api.rejectBooking(bookingId)

    override suspend fun startBooking(bookingId: String): BookingResponseDTO =
        api.startBooking(bookingId)

    override suspend fun markFinishPending(bookingId: String): BookingResponseDTO =
        api.markFinishPending(bookingId)

    override suspend fun confirmFinished(bookingId: String): BookingResponseDTO =
        api.confirmFinished(bookingId)

    override suspend fun denyFinish(bookingId: String): BookingResponseDTO =
        api.denyFinish(bookingId)
}

