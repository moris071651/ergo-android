package com.moris.ergo.data.scheme

data class ListingBriefInfo(
    val id: String,
    val title: String,
    val rating: Double,
    val priceString: String,
    val description: String,
    val primaryImageUrl: String
)
