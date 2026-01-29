package com.moris.ergo.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.moris.ergo.ui.widgets.common.GreetingAndSearch
import com.moris.ergo.ui.widgets.listings.ListingBriefInfoCarousel
import com.moris.ergo.ui.widgets.common.SectionHeader
import com.moris.ergo.ui.widgets.workers.WorkerBriefInfoCarousel

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
