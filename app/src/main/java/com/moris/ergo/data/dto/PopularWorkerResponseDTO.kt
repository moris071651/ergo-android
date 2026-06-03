package com.moris.ergo.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PopularWorkerResponseDTO(
    @SerialName("user_id")
    val userId: String,

    val bio: String? = null,

    @SerialName("profile_image_url")
    val profileImageUrl: String? = null,

    @SerialName("service_radius_km")
    val serviceRadiusKm: Int? = null,

    @SerialName("experience_years")
    val experienceYears: Int? = null,

    val skills: List<String>,
    val available: Boolean,

    @SerialName("created_at")
    val createdAt: String,

    val user: UserResponseDTO
)
