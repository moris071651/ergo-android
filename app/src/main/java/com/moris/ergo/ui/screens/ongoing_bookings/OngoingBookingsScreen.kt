package com.moris.ergo.ui.screens.ongoing_bookings

import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.moris.ergo.data.dto.BookingResponseDTO
import androidx.core.net.toUri

@Composable
fun OngoingBookingsScreen(
    viewModel: OngoingBookingsViewModel = hiltViewModel(),
    onBookingClick: (String) -> Unit,
    onListingClick: (String) -> Unit
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val customerBookings by viewModel.customerBookings.collectAsState()
    val workerBookings by viewModel.workerBookings.collectAsState()
    val customerUnauthorized by viewModel.customerUnauthorized.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(workerBookings) {
        viewModel.loadBookings()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            error != null -> {
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            text = "My Bookings",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    when {
                        customerUnauthorized -> {
                            item {
                                Text(
                                    text = "Please login to see your bookings",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                        customerBookings.isNullOrEmpty() -> {
                            item {
                                Text(
                                    text = "No bookings yet",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        else -> {
                            items(customerBookings!!) { booking ->
                                BookingCard(
                                    booking = booking,
                                    onClick = { onBookingClick(booking.id) },
                                    onListingClick = { onListingClick(booking.listing.id) }
                                )
                            }
                        }
                    }

                    if (!workerBookings.isNullOrEmpty()) {
                        item {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "Bookings as Worker",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        items(workerBookings!!) { booking ->
                            BookingCard(
                                booking = booking,
                                onClick = { onBookingClick(booking.id) },
                                onListingClick = { onListingClick(booking.listing.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookingCard(
    booking: BookingResponseDTO,
    onClick: () -> Unit,
    onListingClick: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = booking.listing.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                        .clickable(onClick = onListingClick)
                )
                Text(
                    text = booking.listing.priceString,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("State:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                    Text(booking.state.toString(), style = MaterialTheme.typography.bodySmall)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("From:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                    Text(booking.startAt, style = MaterialTheme.typography.bodySmall)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("To:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                    Text(booking.endAt, style = MaterialTheme.typography.bodySmall)
                }
            }

            Text(
                text = "Address: ${booking.address.label}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .clickable {
                        openMap(context, booking.address.lat, booking.address.lon, booking.address.label)
                    }
                    .padding(vertical = 2.dp)
            )

            booking.reason?.let {
                Text(
                    text = "Reason: $it",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

fun openMap(context: Context, lat: Double, lon: Double, label: String) {
    val uri = "geo:$lat,$lon?q=$lat,$lon($label)".toUri()
    val mapIntent = Intent(Intent.ACTION_VIEW, uri)

    mapIntent.setPackage("com.google.android.apps.maps")

    try {
        context.startActivity(mapIntent)
    } catch (e: Exception) {
        val browserUri = "www.google.com".toUri()
        context.startActivity(Intent(Intent.ACTION_VIEW, browserUri))
    }
}
