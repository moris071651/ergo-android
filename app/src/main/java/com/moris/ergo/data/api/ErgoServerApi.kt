package com.moris.ergo.data.api

import android.content.Context
import com.moris.ergo.data.dto.AddressDTO
import com.moris.ergo.data.dto.BecomeWorkerRequestDTO
import com.moris.ergo.data.dto.BookingResponseDTO
import com.moris.ergo.data.dto.CreateAddressRequestDTO
import com.moris.ergo.data.dto.CreateBookingRequestDTO
import com.moris.ergo.data.dto.CreateBookingResponseDTO
import com.moris.ergo.data.dto.CreateListingRequestDTO
import com.moris.ergo.data.dto.CurrentUserResponseDTO
import com.moris.ergo.data.dto.CurrentWorkerResponseDTO
import com.moris.ergo.data.dto.ListingResponseDTO
import com.moris.ergo.data.dto.ListingResponsePublicDTO
import com.moris.ergo.data.dto.StripeOnboardingLinkDTO
import com.moris.ergo.data.dto.ToggleListingActiveDTO
import com.moris.ergo.data.dto.UserAuthResponseDTO
import com.moris.ergo.data.dto.UserLoginRequestDTO
import com.moris.ergo.data.dto.UserResponseDTO
import com.moris.ergo.data.dto.UserSignupRequestDTO
import com.moris.ergo.data.dto.WorkerResponseDTO
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.serialization.json.Json

@Singleton
class ErgoServerApi @Inject constructor(
    @ApplicationContext context: Context
) {

    private val client = HttpClient(CIO) {
        expectSuccess = true

        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }

        install(HttpCookies) {
            storage = PersistentCookieStorage(context)
//            storage = AcceptAllCookiesStorage()
        }

//        HttpResponseValidator {
//            handleResponseExceptionWithRequest { cause, request ->
//                if (cause is ClientRequestException && cause.response.status == HttpStatusCode.Unauthorized) {
//                    val refreshed = runBlocking { refreshAccessToken() }
//                    if (!refreshed) {
//                        throw cause
//                    }
//                }
//                else {
//                    throw cause
//                }
//            }
//        }
//
//        install(HttpRequestRetry) {
//            retryOnException(maxRetries = 1)
//            retryIf { request, response ->
//                response.status == HttpStatusCode.Unauthorized
//            }
//        }
    }

    private val BASE_URL = "https://ad12f66a1bc4.ngrok-free.app" // Ngrok or deployed API

    suspend fun signup(request: UserSignupRequestDTO): UserAuthResponseDTO {
        return client.post("$BASE_URL/api/v1/auth/signup") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun login(request: UserLoginRequestDTO): UserAuthResponseDTO {
        return client.post("$BASE_URL/api/v1/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun getCurrentUser(): CurrentUserResponseDTO {
        return client.get("$BASE_URL/api/v1/users/me").body()
    }

    suspend fun getUserById(userId: String): UserResponseDTO {
        return client.get("$BASE_URL/api/v1/users/$userId").body()
    }

    suspend fun logout() {
        client.post("$BASE_URL/api/v1/auth/logout")
    }

    suspend fun refreshAccessToken(): Boolean {
        return try {
            val response = client.post("$BASE_URL/api/v1/auth/refresh")
            response.status.isSuccess()
        }
        catch (e: Exception) {
            false
        }
    }
    suspend fun getListings(
        minPrice: Float? = null,
        maxPrice: Float? = null,
        city: String? = null,
        title: String? = null,
        category: String? = null,
        limit: Int? = null
    ): List<ListingResponsePublicDTO> {
        return client.get("$BASE_URL/api/v1/listings") {
            parameter("min_price", minPrice)
            parameter("max_price", maxPrice)
            parameter("city", city)
            parameter("title", title)
            parameter("category", category)
            parameter("limit", limit)
        }.body()
    }

    suspend fun getWorkerListingsById(userId: String): List<ListingResponsePublicDTO> =
        client.get("$BASE_URL/api/v1/workers/$userId/listings").body()

    suspend fun bookListing(listingId: String, body: CreateBookingRequestDTO): CreateBookingResponseDTO =
        client.post("$BASE_URL/api/v1/listings/$listingId/book") {
            contentType(ContentType.Application.Json)
            setBody(body)
        }.body()
    suspend fun getListingDetail(id: String): ListingResponsePublicDTO {
        return client.get("$BASE_URL/api/v1/listings/$id").body()
    }

    suspend fun createAddress(address: CreateAddressRequestDTO): AddressDTO =
        client.post("$BASE_URL/api/v1/users/me/address/") {
            contentType(ContentType.Application.Json)
            setBody(address)
        }.body()

    suspend fun updateAddress(id: String, address: CreateAddressRequestDTO): AddressDTO =
        client.patch("$BASE_URL/api/v1/users/me/address/$id") {
            contentType(ContentType.Application.Json)
            setBody(address)
        }.body()

    suspend fun deleteAddress(id: String) {
        client.delete("$BASE_URL/api/v1/users/me/address/$id")
    }

    suspend fun getAllAddressesCurrentUser(): List<AddressDTO> =
        client.get("$BASE_URL/api/v1/users/me/address/").body()

    suspend fun getAddressById(addressId: String): AddressDTO =
        client.get("$BASE_URL/api/v1/users/me/address/$addressId").body()

    suspend fun getListingById(listingId: String): ListingResponsePublicDTO =
        client.get("$BASE_URL/api/v1/listings/$listingId").body()

    suspend fun becomeWorker(
        request: BecomeWorkerRequestDTO
    ): CurrentWorkerResponseDTO =
        client.post("$BASE_URL/api/v1/workers/me/") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    suspend fun getCurrentWorker(): CurrentWorkerResponseDTO =
        client.get("$BASE_URL/api/v1/workers/me/").body()

    suspend fun getWorkerById(userId: String): WorkerResponseDTO =
        client.get("$BASE_URL/api/v1/workers/$userId").body()

    suspend fun getStripeOnboardingLink(): StripeOnboardingLinkDTO {
        return client.post("$BASE_URL/api/v1/stripe/onboarding").body()
    }

    suspend fun getMyListings(): List<ListingResponseDTO> {
        return client.get("$BASE_URL/api/v1/workers/me/listings/").body()
    }

    suspend fun createListing(request: CreateListingRequestDTO): ListingResponseDTO {
        return client.post("$BASE_URL/api/v1/workers/me/listings/") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun deleteListing(listingId: String) {
        client.delete("$BASE_URL/api/v1/workers/me/listings/$listingId")
    }

    suspend fun toggleListingActive(listingId: String, request: ToggleListingActiveDTO): ListingResponseDTO =
        client.patch("$BASE_URL/api/v1/workers/me/listings/$listingId") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    suspend fun getCurrentUserBookings(): List<BookingResponseDTO> =
        client.get("$BASE_URL/api/v1/users/me/bookings").body()

    suspend fun getCurrentWorkerBookings(): List<BookingResponseDTO> =
        client.get("$BASE_URL/api/v1/workers/me/bookings").body()

    suspend fun getBookingByIdWorker(bookingId: String): BookingResponseDTO =
        client.get("$BASE_URL/api/v1/workers/me/bookings/$bookingId").body()

    suspend fun getBookingByIdCustomer(bookingId: String): BookingResponseDTO =
        client.get("$BASE_URL/api/v1/bookings12/$bookingId").body()

    suspend fun acceptBooking(bookingId: String): BookingResponseDTO =
        client.post("$BASE_URL/api/v1/bookings/$bookingId/approve").body()

    suspend fun rejectBooking(bookingId: String): BookingResponseDTO =
        client.post("$BASE_URL/api/v1/bookings/$bookingId/reject").body()

    suspend fun startBooking(bookingId: String): BookingResponseDTO =
        client.post("$BASE_URL/api/v1/bookings/$bookingId/start").body()

    suspend fun markFinishPending(bookingId: String): BookingResponseDTO =
        client.post("$BASE_URL/api/v1/bookings/$bookingId/finish").body()

    suspend fun confirmFinished(bookingId: String): BookingResponseDTO =
        client.post("$BASE_URL/api/v1/bookings/$bookingId/finish/confirm").body()

    suspend fun denyFinish(bookingId: String): BookingResponseDTO =
        client.post("$BASE_URL/api/v1/bookings/$bookingId/finish/deny").body()
}
