package com.moris.ergo.data.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BecomeWorkerRequestDTO(
    @SerialName("address_id") val addressId: String,
    val bio: String,
    @SerialName("service_radius_km") val serviceRadiusKm: Int,
    @SerialName("experience_years") val experienceYears: Int,
    val skills: List<String>
)

@Serializable
data class CurrentWorkerResponseDTO(
    @SerialName("user_id") val userId: String,
    val bio: String,
    @SerialName("service_radius_km") val serviceRadiusKm: Int,
    @SerialName("experience_years") val experienceYears: Int,
    val skills: List<String>,
    val available: Boolean,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
    @SerialName("stripe_account_id") val stripeAccountId: String?,
    @SerialName("charges_enabled") val chargesEnabled: Boolean,
    @SerialName("payouts_enabled") val payoutsEnabled: Boolean
)

@Serializable
data class WorkerResponseDTO(
    @SerialName("user_id") val userId: String,
    val bio: String,
    @SerialName("service_radius_km") val serviceRadiusKm: Int,
    @SerialName("experience_years") val experienceYears: Int,
    val skills: List<String>,
    val available: Boolean,
    @SerialName("created_at") val createdAt: String,
)

@Serializable
data class StripeOnboardingLinkDTO(val url: String)
