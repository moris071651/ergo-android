package com.moris.ergo.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateListingRequestDTO(
    val title: String,
    val description: String,
    @SerialName("price_cents") val priceCents: Int,
    @SerialName("currency_iso_code") val currencyIsoCode: String = "EUR",
    @SerialName("allow_recurring") val allowRecurring: Boolean,
    @SerialName("visit_required") val visitRequired: Boolean,
    @SerialName("duration_days") val durationDays: Int
)

@Serializable
data class ListingResponseDTO(
    val id: String,
    @SerialName("owner_id") val ownerId: String,
    val title: String,
    val description: String,
    @SerialName("price_cents") val priceCents: Int,
    @SerialName("currency_iso_code") val currencyIsoCode: String,
    @SerialName("price_str") val priceStr: String,
    @SerialName("allow_recurring") val allowRecurring: Boolean,
    @SerialName("visit_required") val visitRequired: Boolean,
    @SerialName("duration_days") val durationDays: Int,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
    @SerialName("is_active") val isActive: Boolean
)

@Serializable
data class ToggleListingActiveDTO(
    @SerialName("is_active") val isActive: Boolean
)