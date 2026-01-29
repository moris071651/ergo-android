package com.moris.ergo.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ListingResponsePublicDTO(
    @SerialName("id") val id: String,
    @SerialName("owner_id") val ownerId: String,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String? = null,
    @SerialName("price_cents") val priceCents: Int,
    @SerialName("currency_iso_code") val currencyIsoCode: String,
    @SerialName("price_str") val priceString: String,
    @SerialName("visit_required") val visitRequired: Boolean,
    @SerialName("duration_days") val durationDays: Int,
    @SerialName("created_at") val createdAt: String
)

@Serializable
data class ListingResponseOwnerDTO(
    @SerialName("id") val id: String,
    @SerialName("owner_id") val ownerId: String,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String? = null,
    @SerialName("price_cents") val priceCents: Int,
    @SerialName("currency_iso_code") val currencyIsoCode: String,
    @SerialName("price_str") val priceString: String,
    @SerialName("visit_required") val visitRequired: Boolean,
    @SerialName("duration_days") val durationDays: Int,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
    @SerialName("is_active") val isActive: String
)
