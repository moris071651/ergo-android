package com.moris.ergo.data.mapper

import com.moris.ergo.data.dto.ListingResponsePublicDTO
import com.moris.ergo.data.scheme.ListingBriefInfo

fun ListingResponsePublicDTO.toListingBriefInfo(): ListingBriefInfo {
    return ListingBriefInfo(
        id = this.id,
        title = this.title,
        rating = 4.8,
        priceString = this.priceString,
        description = this.description ?: "",
        primaryImageUrl = ""
    )
}
