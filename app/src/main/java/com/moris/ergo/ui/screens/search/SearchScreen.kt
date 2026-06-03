package com.moris.ergo.ui.screens.search

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.SubcomposeAsyncImage
import com.moris.ergo.data.scheme.ListingBriefInfo
import java.util.Locale

@Composable
fun SearchScreen(
    onListingClick: (String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val results by viewModel.results.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    SearchScreenContent(
        results = results,
        isLoading = isLoading,
        onListingClick = onListingClick
    )
}

@Composable
private fun SearchScreenContent(
    onListingClick: (String) -> Unit = {},
    results: List<ListingBriefInfo>,
    isLoading: Boolean
) {
    Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            if (results.isEmpty() && !isLoading) {
                EmptySearchState()
            }
            else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(results) { listing ->
                        ListingSearchCard(
                            listing = listing,
                            onClick = { onListingClick(listing.id) }
                        )
                    }
                }
            }
        }

        if (results.isEmpty() && !isLoading) {
            EmptySearchState()
        }
        else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(results) { listing ->
                    ListingSearchCard(
                        listing = listing,
                        onClick = { onListingClick(listing.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ListingSearchCard(
    listing: ListingBriefInfo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box {
                SubcomposeAsyncImage(
                    model = listing.primaryImageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.height(160.dp).fillMaxWidth(),
                    loading = {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(strokeWidth = 2.dp)
                        }
                    },
                    error = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surfaceVariant), // Soft gray/neutral
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = "Default Image",
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                    }
                )

                Surface(
                    modifier = Modifier.padding(12.dp).align(Alignment.TopEnd),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = listing.priceString,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = listing.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = listing.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun EmptySearchState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
        Text("Start searching for workers", color = Color.Gray)
    }
}

@Preview(showBackground = true, name = "Search Screen - Results")
@Composable
fun PreviewSearchScreen() {
    MaterialTheme {
        val mockListings = listOf(
            ListingBriefInfo(
                id = "1",
                title = "Professional House Cleaning",
                description = "Deep cleaning services for apartments and houses. We bring our own supplies and ensure a spotless home.",
                priceString = "$45/hr",
                rating = 4.8,
                primaryImageUrl = "https://example.com"
            ),
            ListingBriefInfo(
                id = "2",
                title = "Expert Plumbing Repair",
                description = "24/7 emergency plumbing. Leaks, clogs, and installations handled by certified professionals.",
                priceString = "$80/hr",
                rating = 4.9,
                primaryImageUrl = "https://example.com"
            )
        )

        Surface(modifier = Modifier.fillMaxSize()) {
            SearchScreenContent(
                results = mockListings,
                isLoading = false,
            )
        }
    }
}

@Preview(showBackground = true, name = "Search Screen - Empty State")
@Composable
fun PreviewSearchEmpty() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            SearchScreenContent(
                results = emptyList(),
                isLoading = false,
            )
        }
    }
}
