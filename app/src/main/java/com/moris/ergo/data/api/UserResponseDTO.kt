package com.moris.ergo.data.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserResponseDTO(
    val id: String,
    val email: String,
    @SerialName("profile_image_url") val profileImageUrl: String? = null,
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String,
    @SerialName("created_at") val createdAt: String,
)

@Serializable
data class CurrentUserResponseDTO(
    val id: String,
    val email: String,
    @SerialName("profile_image_url") val profileImageUrl: String? = null,
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String
)
