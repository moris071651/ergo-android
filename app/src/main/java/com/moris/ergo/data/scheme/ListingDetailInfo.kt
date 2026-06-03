package com.moris.ergo.data.scheme

data class ListingDetailInfo(
    val id: String,
    val ownerId: String,
    val title: String,
    val description: String,
    val priceString: String,
    val rating: Double,
    val visitRequired: Boolean,
    val durationDays: Int,
    val imageUrls: List<String>
)
