package com.moris.ergo.ui.screens.home

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.moris.ergo.data.scheme.WorkerBriefInfo
import com.moris.ergo.ui.widgets.listings.ListingBriefInfoCarousel
import com.moris.ergo.ui.widgets.common.SectionHeader
import com.moris.ergo.ui.widgets.users.UserAvatarIcon

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onSearchClick: (() -> Unit),
    onWorkerClick: ((String) -> Unit),
    onListingClick: ((String) -> Unit)
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val popularWorkers by viewModel.popularWorkers.collectAsState()
    val popularListings by viewModel.popularListings.collectAsState()

    if(isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
    else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                GreetingAndSearch(null, onSearchClick)
                Spacer(Modifier.height(12.dp))
            }

            item {
                SectionHeader("Popular Workers")
                WorkerBriefInfoCarousel(popularWorkers, onWorkerClick)
                Spacer(Modifier.height(12.dp))
            }

            item {
                SectionHeader("Popular Listings")
                ListingBriefInfoCarousel(popularListings, onListingClick)
            }
        }
    }
}

@Composable
fun GreetingAndSearch(name: String? = null, onSearchClick: () -> Unit) {
    val greeting = if (name != null) "Hi $name" else "Hi"

    val colors = TextFieldDefaults.colors()
    val backgroundColor = colors.unfocusedContainerColor
    val textColor = colors.unfocusedTextColor

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "$greeting, find your worker",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(
                    color = backgroundColor,
                    shape = MaterialTheme.shapes.medium
                )
                .clickable(onClick = onSearchClick),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = "Search for jobs, workers...",
                modifier = Modifier.padding(start = 16.dp),
                color = textColor
            )
        }
    }
}

@Composable
fun WorkerBriefInfoCarousel(
    workers: List<WorkerBriefInfo>,
    onClick: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(workers) { worker ->
            WorkerBriefInfoCard(
                worker = worker,
                onClick = { onClick(worker.userId) }
            )
        }
    }
}

@Composable
@SuppressLint("DefaultLocale")
fun WorkerBriefInfoCard(
    worker: WorkerBriefInfo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(270.dp)
            .height(180.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp)
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

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = worker.description,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "${worker.yearsOfExperience} years of experience",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
