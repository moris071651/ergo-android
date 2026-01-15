package com.moris.ergo.data.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateBookingRequestDTO(
    @SerialName("start_at") val startAt: String,
    @SerialName("end_at") val endAt: String,
    @SerialName("address_id") val addressId: String
)

@Serializable
data class CreateBookingResponseDTO(
    val id: String
)

@Serializable
data class BookingResponseDTO(
    val id: String,
    val state: BookingState,
    @SerialName("start_at") val startAt: String,
    @SerialName("end_at") val endAt: String,
    @SerialName("created_at") val createdAt: String,
    val address: AddressDTO,
    val listing: ListingResponsePublicDTO,
    val reason: String? = null,
    @SerialName("worker_id") val workerId: String? = null,
    @SerialName("customer_id") val customerId: String? = null,
    @SerialName("client_secret") val clientSecret: String? = null
)

@Serializable
enum class BookingState {
    @SerialName("on-hold") ON_HOLD,
    @SerialName("created") CREATED,
    @SerialName("waiting-approval") WAITING_APPROVAL,
    @SerialName("pending-payment") PENDING_PAYMENT,
    @SerialName("pending") PENDING,
    @SerialName("in-progress") IN_PROGRESS,
    @SerialName("canceled") CANCELLED,
    @SerialName("finished") FINISHED,
    @SerialName("finish-pending") FINISH_PENDING
}

fun BookingState.humanReadable(): String =
    when (this) {
        BookingState.ON_HOLD -> "On hold"
        BookingState.CREATED -> "Created"
        BookingState.WAITING_APPROVAL -> "Waiting for worker approval"
        BookingState.PENDING_PAYMENT -> "Payment required"
        BookingState.PENDING -> "Scheduled"
        BookingState.IN_PROGRESS -> "Work in progress"
        BookingState.FINISH_PENDING -> "Waiting for customer confirmation"
        BookingState.FINISHED -> "Completed"
        BookingState.CANCELLED -> "Canceled"
    }
