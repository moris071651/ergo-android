package com.moris.ergo.data.scheme

data class WorkerSummaryInfo(
    val id: String,
    val name: String,
    val skill: String,
    val pfpUrl: String,
    val rating: Double,
    val yearsOfExperience: Int
)
