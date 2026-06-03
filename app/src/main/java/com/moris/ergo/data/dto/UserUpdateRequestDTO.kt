package com.moris.ergo.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserUpdateRequestDTO(
    val email: String,
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String
)