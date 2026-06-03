package com.moris.ergo.ui.screens.my_listings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.SubcomposeAsyncImage
import com.moris.ergo.TitleViewModel

@Composable
fun MyListingsScreen(
    viewModel: MyListingsViewModel = hiltViewModel(),
    titleViewModel: TitleViewModel = hiltViewModel(),
    onEditClick: (String) -> Unit,
    onCardClick: (String) -> Unit,
    onCreateClick: () -> Unit
) {
    val listings by viewModel.listings.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        titleViewModel.setTitle("My Listings")
        viewModel.loadListings()
    }

    MyListingsContent(
        listings = listings,
        isLoading = isLoading,
        error = error,
        onCreateClick = onCreateClick,
        onToggleActive = { id, isActive -> viewModel.toggleListingActive(id, isActive) },
        onDeleteClick = { id -> viewModel.deleteListing(id) },
        onEditClick = onEditClick,
        onCardClick = onCardClick
    )
}

@Composable
fun MyListingsContent(
    listings: List<MyListingItem>,
    isLoading: Boolean,
    error: String?,
    onCreateClick: () -> Unit,
    onToggleActive: (String, Boolean) -> Unit,
    onDeleteClick: (String) -> Unit,
    onEditClick: (String) -> Unit,
    onCardClick: (String) -> Unit
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateClick,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Create Listing")
            }
        }
    ) { _ ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                listings.isEmpty() -> {
                    MyListingsEmptyState(modifier = Modifier.align(Alignment.Center))
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(listings, key = { it.id }) { listing ->
                            MyListingManageCard(
                                listing = listing,
                                onToggleActive = { onToggleActive(listing.id, listing.isActive) },
                                onDeleteClick = { onDeleteClick(listing.id) },
                                onEditClick = { onEditClick(listing.id) },
                                onCardClick = { onCardClick(listing.id) }
                            )
                        }
                    }
                }
            }

            error?.let {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MyListingManageCard(
    listing: MyListingItem,
    onToggleActive: () -> Unit,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit,
    onCardClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable() { onCardClick() },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Column() {
            ManageCardImageBanner(imageUrls = listing.imageUrls)

            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = listing.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = listing.priceStr,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = listing.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Switch(
                            checked = listing.isActive,
                            onCheckedChange = { onToggleActive() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                                checkedBorderColor = Color.Transparent,

                                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                                uncheckedBorderColor = Color.Transparent
                            )
                        )
                        Text(
                            text = if (listing.isActive) "Active" else "Inactive",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = if (listing.isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row {
                        IconButton(
                            onClick = onEditClick,
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Service Listing"
                            )
                        }

                        IconButton(
                            onClick = onDeleteClick,
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Service Listing"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ManageCardImageBanner(imageUrls: List<String>) {
    val pagerState = rememberPagerState(pageCount = { imageUrls.size })

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
    ) {
        if (imageUrls.isNotEmpty()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                SubcomposeAsyncImage(
                    model = imageUrls[page],
                    contentDescription = "Listing image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    loading = {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(24.dp))
                        }
                    },
                    error = {
                        Box(
                            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        }
                    }
                )
            }

            Row(
                Modifier
                    .wrapContentSize()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 10.dp)
                    .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(imageUrls.size) { iteration ->
                    val color = if (pagerState.currentPage == iteration) Color.White else Color.White.copy(alpha = 0.5f)
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(color)
                            .size(5.dp)
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No images attached",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun MyListingsEmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = "No listings yet",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Tap the + button below to create your first service.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true, name = "My Listings Management Screen")
@Composable
fun MyListingsPreview() {
    val sampleListings = listOf(
        MyListingItem(
            id = "lst_1a2b3c4d5",
            title = "Suburban Backyard Event Space",
            description = "Spacious backyard perfect for small gatherings, birthday parties, and weekend barbecues.",
            priceStr = "$450",
            isActive = false,
            imageUrls = listOf(),
        ),
        MyListingItem(
            id = "lst_9x82j3f1k",
            title = "Cozy Downtown Studio Apartment",
            description = "A fully furnished modern studio located in the heart of the city. Includes high-speed internet and utilities.",
            priceStr = "$1,200",
            isActive = true,
            imageUrls = listOf()
        )
    )

    MaterialTheme {
        MyListingsContent(
            listings = sampleListings,
            isLoading = false,
            error = null,
            onCreateClick = {},
            onToggleActive = { _, _ -> },
            onDeleteClick = {},
            onEditClick = {},
            onCardClick = {}
        )
    }
}
