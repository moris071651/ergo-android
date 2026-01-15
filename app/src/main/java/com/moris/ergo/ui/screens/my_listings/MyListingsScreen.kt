package com.moris.ergo.ui.screens.my_listings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun MyListingsScreen(
    viewModel: MyListingsViewModel = hiltViewModel(),
    onCreateClick: () -> Unit
) {
    val listings by viewModel.listings.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadListings() }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateClick) {
                Text("+")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when {
                isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                listings.isEmpty() -> Text("No listings yet", Modifier.align(Alignment.Center))
                else -> LazyColumn {
                    items(listings, key = { it.id }) { listing ->
                        Card(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text(listing.title, fontWeight = FontWeight.Bold)
                                Text(listing.description)
                                Text(listing.priceStr)
                                Spacer(Modifier.height(8.dp))
                                Button(
                                    onClick = { viewModel.toggleListingActive(listing.id, listing.isActive) }
                                ) {
                                    if(listing.isActive) {
                                        Text("Deactivate")
                                    }
                                    else {
                                        Text("Activate")
                                    }
                                }
                                Button(
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                    onClick = { viewModel.deleteListing(listing.id) }
                                ) {
                                    Text("Delete")
                                }
                            }
                        }
                    }
                }
            }

            error?.let {
                Text(it, color = Color.Red, modifier = Modifier.align(Alignment.BottomCenter))
            }
        }
    }
}
