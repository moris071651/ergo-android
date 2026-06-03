package com.moris.ergo.ui.screens.my_listings

import com.moris.ergo.data.dto.ListingResponseDTO
import com.moris.ergo.data.dto.ListingResponsePublicDTO

data class MyListingItem(
    val id: String,
    val title: String,
    val description: String,
    val priceStr: String,
    val priceCents: Int? = null,
    val isActive: Boolean,
    var imageUrls: List<String>
)

fun ListingResponseDTO.toMyListingItemUiState(): MyListingItem {
    return MyListingItem(
        id = this.id,
        title = this.title,
        description = this.description,
        priceStr = this.priceStr,
        isActive = this.isActive,
        imageUrls = listOf()
    )
}

fun ListingResponsePublicDTO.toMyListingItemUiState(): MyListingItem {
    return MyListingItem(
        id = this.id,
        title = this.title,
        description = this.description ?: "",
        priceStr = this.priceString,
        priceCents = this.priceCents,
        isActive = true, // no match
        imageUrls = listOf()
    )
}
