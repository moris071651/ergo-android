package com.moris.ergo.ui.widgets.listings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.SubcomposeAsyncImage
import com.moris.ergo.data.scheme.ListingBriefInfo

@Composable
fun ListingBriefInfoCard(
    listing: ListingBriefInfo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .height(246.dp)
            .clickable(onClick = onClick)
            .background(MaterialTheme.colorScheme.background),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (listing.primaryImageUrl.isNotBlank()) {
                CardImageHeader(primaryImageUrl = listing.primaryImageUrl)
            }
            else {
                NoImagesPlaceholder()
            }

            CardBodyMetadata(
                title = listing.title,
                priceString = listing.priceString,
                rating = listing.rating,
                description = listing.description
            )
        }
    }
}

@Composable
fun NoImagesPlaceholder() {
    Box(
        modifier = Modifier
            .height(135.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLow),
        contentAlignment = Alignment.Center
    ) {
        Text("No Image Available", style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun CardImageHeader(primaryImageUrl: String) {
    SubcomposeAsyncImage(
        model = primaryImageUrl,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .height(135.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLow),
        loading = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        error = {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Image load error",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(34.dp)
                )

                Spacer(Modifier.height(13.dp))

                Text(
                    text = "Unable to load image",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}

@Composable
fun ColumnScope.CardBodyMetadata(
    title: String,
    priceString: String,
    rating: Double,
    description: String
) {
    Column(
        modifier = Modifier.weight(1f).padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 20.sp
        )

        CardMetricsRow(priceString = priceString, rating = rating)

        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 16.sp
        )
    }
}

@Composable
fun CardMetricsRow(priceString: String, rating: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = priceString,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview(showBackground = true, name = "Listing Card Item Representation")
@Composable
fun ListingCardPreview() {
    val sampleCardData = ListingBriefInfo(
        id = "91",
        title = "Plumbing Emergency Fix",
        description = "Full home piping inspection, leakage seal patching, bathroom blockages removals, and line testing services.",
        priceString = "$85",
        rating = 4.87,
        primaryImageUrl = ""
    )
    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ListingBriefInfoCard(
                listing = sampleCardData,
                onClick = {}
            )
        }
    }
}
