package com.moris.ergo.ui.screens.booking_detail

import android.content.Context
import android.util.Log
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.moris.ergo.data.dto.AddressDTO
import com.moris.ergo.data.dto.BookingResponseDTO
import com.moris.ergo.data.dto.BookingState
import com.moris.ergo.data.dto.ListingResponsePublicDTO
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheet.Builder
import com.stripe.android.paymentsheet.PaymentSheetResult

@Composable
fun BookingDetailScreen(
    bookingId: String,
    onPaymentSuccess: () -> Unit,
    onContactClick: (String, Context) -> Unit,
    viewModel: BookingDetailViewModel = hiltViewModel()
) {
    val booking by viewModel.booking.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isWorker by viewModel.isWorker.collectAsState()
    val workerEmail by viewModel.workerEmail.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(bookingId) {
        viewModel.loadBooking(bookingId)
    }

    val paymentResultCallback = { result: PaymentSheetResult ->
        when (result) {
            is PaymentSheetResult.Completed -> {
                viewModel.loadBooking(bookingId)
                onPaymentSuccess()
            }
            is PaymentSheetResult.Canceled -> {}
            is PaymentSheetResult.Failed -> {
                viewModel.setError(result.error.localizedMessage)
            }
        }
    }

    val paymentSheet = remember(paymentResultCallback) { Builder(paymentResultCallback) }.build()
    val onPayAction: (String) -> Unit = { clientSecret ->
        paymentSheet.presentWithPaymentIntent(
            paymentIntentClientSecret = clientSecret,
            configuration = PaymentSheet.Configuration(
                merchantDisplayName = "Ergo",
                allowsDelayedPaymentMethods = true
            )
        )
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
                Column(
                    modifier = Modifier.align(Alignment.Center).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = error!!, color = MaterialTheme.colorScheme.error)
                    Button(onClick = { viewModel.loadBooking(bookingId) }) {
                        Text("Retry")
                    }
                }
            }
            booking != null -> {
                BookingDetailContent(
                    booking = booking!!,
                    isWorker = isWorker == true,
                    workerEmail = workerEmail,
                    onPay = onPayAction,
                    onContactClick = { onContactClick(it, context) },
                    onAccept = { viewModel.acceptBooking(booking!!.id) },
                    onReject = { viewModel.rejectBooking(booking!!.id) },
                    onStartWork = { viewModel.startBooking(booking!!.id) },
                    onFinishPending = { viewModel.markFinishPending(booking!!.id) },
                    onConfirmFinish = { viewModel.confirmFinished(booking!!.id) },
                    onDenyFinish = { viewModel.denyFinish(booking!!.id) }
                )
            }
        }
    }
}

@Composable
fun BookingDetailContent(
    booking: BookingResponseDTO,
    isWorker: Boolean,
    workerEmail: String?,
    onContactClick: (String) -> Unit,
    onPay: (String) -> Unit,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onStartWork: () -> Unit,
    onFinishPending: () -> Unit,
    onConfirmFinish: () -> Unit,
    onDenyFinish: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = booking.listing.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        BookingStateChip(booking.state)
                    }
                    Text(
                        text = booking.listing.priceString,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Schedule Details", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                        Text("From: ${booking.startAt}", style = MaterialTheme.typography.bodyMedium)
                        Text("To: ${booking.endAt}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            if (!workerEmail.isNullOrEmpty()) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Worker Email",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = workerEmail,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable() {
                                onContactClick(workerEmail)
                            }
                        )
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Service Address",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(booking.address.label, fontWeight = FontWeight.Medium)
                    Text(
                        text = "${booking.address.street ?: ""} ${booking.address.houseNumber ?: ""}\n${booking.address.city ?: ""}, ${booking.address.country ?: ""}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }



            booking.reason?.let {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(14.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        Surface(
//            tonalElevation = 4.dp,
//            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                BookingPrimaryAction(
                    booking = booking,
                    isWorker = isWorker,
                    onPay = onPay,
                    onAccept = onAccept,
                    onReject = onReject,
                    onStartWork = onStartWork,
                    onFinishPending = onFinishPending,
                    onConfirmFinish = onConfirmFinish,
                    onDenyFinish = onDenyFinish
                )
            }
        }
    }
}

@Composable
fun BookingPrimaryAction(
    booking: BookingResponseDTO,
    isWorker: Boolean,
    onPay: (String) -> Unit,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onStartWork: () -> Unit,
    onFinishPending: () -> Unit,
    onConfirmFinish: () -> Unit,
    onDenyFinish: () -> Unit
) {
    val actionModifier = Modifier.fillMaxWidth().height(50.dp)

    when (booking.state) {
        BookingState.WAITING_APPROVAL -> {
            if (isWorker) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        modifier = Modifier.weight(1f).height(50.dp),
                        onClick = onAccept
                    ) {
                        Text("Accept")
                    }

                    OutlinedButton(
                        modifier = Modifier.weight(1f).height(50.dp),
                        onClick = onReject
                    ) {
                        Text("Reject")
                    }
                }
            }
        }
        BookingState.PENDING -> {
            if (isWorker) {
                Button(modifier = actionModifier, onClick = onStartWork) {
                    Text("Start work", fontWeight = FontWeight.Bold)
                }
            }
        }
        BookingState.PENDING_PAYMENT -> {
            if (!isWorker && booking.clientSecret != null) {
                Button(modifier = actionModifier, onClick = { onPay(booking.clientSecret) }) {
                    Text("Pay now", fontWeight = FontWeight.Bold)
                }
            }
        }
        BookingState.IN_PROGRESS -> {
            if (isWorker) {
                Button(modifier = actionModifier, onClick = onFinishPending) {
                    Text("Mark as finished", fontWeight = FontWeight.Bold)
                }
            }
        }
        BookingState.FINISH_PENDING -> {
            if (!isWorker) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(modifier = actionModifier, onClick = onConfirmFinish) {
                        Text("Confirm completion")
                    }

                    OutlinedButton(modifier = actionModifier, onClick = onDenyFinish) {
                        Text("Reject completion")
                    }
                }
            }
        }
        else -> { }
    }
}

@Composable
fun BookingStateChip(state: BookingState) {
    val containerColor = when (state) {
        BookingState.WAITING_APPROVAL -> MaterialTheme.colorScheme.tertiaryContainer
        BookingState.IN_PROGRESS -> Color(0xFFE3F2FD)
        BookingState.FINISH_PENDING -> Color(0xFFFFF3E0)
        else -> MaterialTheme.colorScheme.secondaryContainer
    }

    val contentColor = when (state) {
        BookingState.WAITING_APPROVAL -> MaterialTheme.colorScheme.onTertiaryContainer
        BookingState.IN_PROGRESS -> Color(0xFF0D47A1)
        BookingState.FINISH_PENDING -> Color(0xFFE65100)
        else -> MaterialTheme.colorScheme.onSecondaryContainer
    }

    Surface(shape = MaterialTheme.shapes.small, color = containerColor) {
        Text(
            color = contentColor,
            text = state.name.replace("_", " "),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
        )
    }
}

@Preview(showBackground = true, name = "Booking Details - Client Flow")
@Composable
fun DetailContentPreview() {
    val mockListing = ListingResponsePublicDTO(
        id = "lst_001",
        ownerId = "owner_123",
        title = "Deep Apartment Cleaning",
        description = "Complete top-to-bottom dusting, mopping, and disinfecting service.",
        priceCents = 15000,
        currencyIsoCode = "USD",
        priceString = "$150.00",
        visitRequired = false,
        durationDays = 1,
        createdAt = "2026-05-31T12:00:00Z"
    )

    val mockBooking = BookingResponseDTO(
        id = "bk_991",
        state = BookingState.IN_PROGRESS,
        startAt = "2026-06-15",
        endAt = "2026-06-18",
        reason = "Please inspect completion clean-up thoroughly.",
        clientSecret = null,
        listing = mockListing,
        address = AddressDTO(
            id = "1",
            lon = 0.0,
            lat = 0.0,
            label = "Apartment 4B",
            street = "Wall St",
            houseNumber = "12",
            city = "New York",
            country = "USA"
        ),
        createdAt = "2026-06-15",
        workerId = "none",
        customerId = "none"
    )

    MaterialTheme {
        BookingDetailContent(
            booking = mockBooking,
            isWorker = true,
            workerEmail = "null@example.com",
            onPay = {}, onAccept = {}, onReject = {}, onStartWork = {}, onFinishPending = {}, onConfirmFinish = {}, onDenyFinish = {}, onContactClick = {}
        )
    }
}
