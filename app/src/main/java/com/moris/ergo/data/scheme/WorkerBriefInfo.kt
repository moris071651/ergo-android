package com.moris.ergo.data.scheme

data class WorkerBriefInfo(
    val userId: String,
    val pfpUrl: String? = null,
    val name: String,
    val skill: String,
    val rating: Double,
    val description: String,
    val yearsOfExperience: Int
)

data class WorkerDetailInfo(
    val userId: String,
    val email: String,
    val pfpUrl: String? = null,
    val name: String,
    val skill: String,
    val rating: Double,
    val serviceRadiusKm: Int,
    val isAvailable: Boolean,
    val description: String,
    val yearsOfExperience: Int
)
