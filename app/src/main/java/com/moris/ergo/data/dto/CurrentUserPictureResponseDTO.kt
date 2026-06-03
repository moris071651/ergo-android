package com.moris.ergo.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class CurrentUserPictureResponseDTO(
    @SerialName("profile_image_url") val profileImageUrl: String,
)
