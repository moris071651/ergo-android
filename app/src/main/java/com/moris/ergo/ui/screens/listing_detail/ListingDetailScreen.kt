package com.moris.ergo.ui.screens.listing_detail

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.SubcomposeAsyncImage
import com.moris.ergo.TitleViewModel
import com.moris.ergo.data.scheme.ListingDetailInfo
import com.moris.ergo.data.scheme.WorkerSummaryInfo
import com.moris.ergo.ui.widgets.users.UserAvatarIcon


@Composable
fun ListingDetailScreen(
    listingId: String,
    onOwnerClick: (String) -> Unit,
    onBookingClick: () -> Unit,
    titleViewModel: TitleViewModel = hiltViewModel(),
    viewModel: ListingDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(listingId) {
        titleViewModel.setTitle("Listing Detail")
        viewModel.loadListing(listingId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            uiState.error != null -> {
                Text(
                    text = uiState.error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            uiState.listing != null && uiState.worker != null -> {
                ListingDetailScreenContent(
                    listing = uiState.listing!!,
                    worker = uiState.worker!!,
                    actionBarEnabled = uiState.customerUnauthenticated,
                    onOwnerClick = onOwnerClick,
                    onBookingClick = onBookingClick
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListingDetailScreenContent(
    listing: ListingDetailInfo,
    worker: WorkerSummaryInfo,
    actionBarEnabled: Boolean,
    onOwnerClick: (String) -> Unit,
    onBookingClick: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { listing.imageUrls.size })

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                ListingImageBanner(
                    imageUrls = listing.imageUrls,
                    pagerState = pagerState
                )
            }

            item {
                ListingHeaderSection(
                    title = listing.title,
                    rating = listing.rating,
                    priceString = listing.priceString
                )
            }

            item {
                ListingWorkerCardSection(
                    worker = worker,
                    ownerId = listing.ownerId,
                    onOwnerClick = onOwnerClick
                )
            }

            item {
                ListingDescriptionSection(
                    description = listing.description
                )
            }

            item {
                ListingRulesCard(
                    visitRequired = listing.visitRequired,
                    durationDays = listing.durationDays
                )
            }

            item { Spacer(Modifier.height(0.dp)) }
        }

        ListingBottomActionBar(
            onBookingClick = onBookingClick,
            enabled = actionBarEnabled
        )
    }
}

@Composable
fun ListingImageBanner(
    imageUrls: List<String>,
    pagerState: PagerState = rememberPagerState(pageCount = { imageUrls.size })
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
    ) {
        if (imageUrls.isNotEmpty()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                SubcomposeAsyncImage(
                    model = imageUrls[page],
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceContainerLow),
                    loading = {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
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

            if (imageUrls.size > 1) {
                PagerIndicatorsOverlay(
                    pageSize = imageUrls.size,
                    currentPage = pagerState.currentPage,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
        else {
            NoImagesPlaceholder()
        }
    }
}

@Composable
fun NoImagesPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Text("No Images Available", style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun ListingHeaderSection(
    title: String,
    rating: Double,
    priceString: String
) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            lineHeight = 32.sp
        )

        Text(
            text = priceString,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ListingWorkerCardSection(
    worker: WorkerSummaryInfo,
    ownerId: String,
    onOwnerClick: (String) -> Unit
) {
    WorkerSmallCard(
        worker = worker,
        modifier = Modifier.padding(horizontal = 20.dp),
        onClick = { onOwnerClick(ownerId) }
    )
}

@Composable
fun WorkerSmallCard(
    worker: WorkerSummaryInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 14.dp,
                vertical = 8.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                UserAvatarIcon(worker.pfpUrl, Modifier.size(48.dp))

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = worker.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = worker.skill.ifEmpty { "General Worker" },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            YearsOfExperienceWidget(worker.yearsOfExperience)
        }
    }
}

@Composable
fun YearsOfExperienceWidget(yearsOfExperience: Int) {
    val ending = if (yearsOfExperience != 1) "s" else ""

    Text(
        text = "$yearsOfExperience year$ending of experience",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun ListingRulesCard(
    visitRequired: Boolean,
    durationDays: Int
) {
    Card(
        modifier = Modifier.padding(horizontal = 20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Service Rules & Details",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Pre-visit Assessment Required", style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = if (visitRequired) "Yes" else "No",
                    fontWeight = FontWeight.Bold,
                    color = if (visitRequired) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Estimated Project Duration", style = MaterialTheme.typography.bodyMedium)
                Text("$durationDays day(s)", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ListingDescriptionSection(
    description: String
) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Description",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 22.sp
        )
    }
}



@Composable
fun PagerIndicatorsOverlay(
    pageSize: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(bottom = 12.dp)
            .background(Color.Black.copy(alpha = 0.3f), CircleShape)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(pageSize) { iteration ->
            val color = if (currentPage == iteration) Color.White else Color.White.copy(alpha = 0.5f)
            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(color)
                    .size(6.dp)
            )
        }
    }
}

@Composable
fun ListingBottomActionBar(
    onBookingClick: () -> Unit,
    enabled: Boolean = true
) {
    Surface(
        tonalElevation = 6.dp,
        shadowElevation = 10.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(
            onClick = onBookingClick,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .height(52.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Text(text = "Book Now", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true, name = "Listing Screen Display Info")
@Composable
fun ListingDetailPreview() {
    val sampleListing = ListingDetailInfo(
        id = "1", ownerId = "own_88", title = "Premium Kitchen Wood Refurbishing & Polish",
        description = "Full service surface cleaning, sanding down, wood grain treatments, filling, and waterproof clear gloss lacquer application for standard home kitchen cabinetry counters.",
        priceString = "$450.00", rating = 4.9, visitRequired = true, durationDays = 3,
        imageUrls = listOf("https://object.pixocial.com/pixocial/dmxffni837f1xrj8pki9xgrl.jpg")
    )
    val sampleWorker = WorkerSummaryInfo(
        name = "Alex Stephenson", pfpUrl = "", rating = 4.85,
        id = "howknows",
        skill = "Plummer",
        yearsOfExperience = 4
    )

    MaterialTheme {
        ListingDetailScreenContent(
            listing = sampleListing, worker = sampleWorker, true, onOwnerClick = {}, onBookingClick = {}
        )
    }
}
