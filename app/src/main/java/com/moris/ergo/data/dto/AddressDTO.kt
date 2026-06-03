package com.moris.ergo.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddressDTO(
    val id: String,
    val lon: Double,
    val lat: Double,
    val label: String,
    val street: String? = null,
    @SerialName("house_number") val houseNumber: String? = null,
    val city: String? = null,
    @SerialName("postal_code") val postalCode: String? = null,
    val country: String? = null,
)

@Serializable
data class CreateAddressRequestDTO(
    val lon: Double,
    val lat: Double,
    val label: String
)
