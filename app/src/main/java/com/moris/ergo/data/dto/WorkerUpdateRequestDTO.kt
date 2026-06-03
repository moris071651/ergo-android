package com.moris.ergo.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WorkerUpdateRequestDTO(
    @SerialName("address_id") val addressId: String?,
    val bio: String?,
    @SerialName("service_radius_km") val serviceRadiusKm: Int?,
    @SerialName("experience_years") val experienceYears: Int?,
    val skills: List<String>?
)