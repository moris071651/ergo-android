package com.moris.ergo.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ListingImageResponseDTO(
    val id: String,
    val url: String,

    @SerialName("is_primary")
    val isPrimary: Boolean,

    @SerialName("sort_order")
    val sortOrder: Int
)
