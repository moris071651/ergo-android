package com.moris.ergo.data.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserSignupRequestDTO(
    val email: String,
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String,
    val password: String
)

@Serializable
data class UserLoginRequestDTO(
    val email: String,
    val password: String
)

@Serializable
data class UserAuthResponseDTO(
    val id: String,
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String,
    val email: String
)

@Serializable
data class AuthSessionInfoDTO(
    val userId: String,
    val issuedAt: String, // ISO8601 string
    val expiresAt: String,
    val jti: String
)
