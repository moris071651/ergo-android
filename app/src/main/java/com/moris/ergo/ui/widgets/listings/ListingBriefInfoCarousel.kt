package com.moris.ergo.ui.widgets.listings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.moris.ergo.data.scheme.ListingBriefInfo

@Composable
fun ListingBriefInfoCarousel(
    listings: List<ListingBriefInfo>,
    onClick: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(listings) { listing ->
            ListingBriefInfoCard(
                listing = listing,
                onClick = { onClick(listing.id) }
            )
        }
    }
}