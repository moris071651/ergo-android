package com.moris.ergo.ui.screens.booking_detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.moris.ergo.data.dto.BookingResponseDTO
import com.moris.ergo.data.dto.BookingState
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheet.Builder
import com.stripe.android.paymentsheet.PaymentSheetResult

@Composable
fun BookingDetailScreen(
    bookingId: String,
    onPaymentSuccess: () -> Unit,
    viewModel: BookingDetailViewModel = hiltViewModel()
) {
    val booking by viewModel.booking.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isWorker by viewModel.isWorker.collectAsState()

    LaunchedEffect(bookingId) {
        viewModel.loadBooking(bookingId)
    }

    when {
        isLoading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        error != null -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(error!!)
            }
        }

        booking != null -> {
            BookingDetailContent(
                booking = booking!!,
                isWorker = isWorker == true,
                viewModel = viewModel,
                onPaymentSuccess = onPaymentSuccess
            )
        }
    }
}

@Composable
fun BookingDetailContent(
    booking: BookingResponseDTO,
    isWorker: Boolean,
    onPaymentSuccess: () -> Unit,
    viewModel: BookingDetailViewModel
) {
    val paymentResultCallback = { result: PaymentSheetResult ->
        when (result) {
            is PaymentSheetResult.Completed -> {
                viewModel.loadBooking(booking.id)
                onPaymentSuccess()
            }

            is PaymentSheetResult.Canceled -> { }
            is PaymentSheetResult.Failed -> {
                viewModel.setError(result.error.localizedMessage)
            }
        }
    }

    val paymentSheet = remember(paymentResultCallback) { Builder(paymentResultCallback) }.build()
    val onPay: (String) -> Unit = {
        paymentSheet.presentWithPaymentIntent(
            paymentIntentClientSecret = it,
            configuration = PaymentSheet.Configuration(
                merchantDisplayName = "Ergo",
                allowsDelayedPaymentMethods = true
            )
        )
    }

    Box(Modifier.fillMaxSize()) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 88.dp),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    booking.listing.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(booking.listing.priceString, color = MaterialTheme.colorScheme.primary)
            }

            item {
                BookingStateChip(booking.state)
            }

            item {
                Text("From: ${booking.startAt}")
                Text("To: ${booking.endAt}")
            }

            item {
                Text(
                    "Address",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(booking.address.label)
                Text("${booking.address.street} ${booking.address.houseNumber}")
                Text("${booking.address.city}, ${booking.address.country}")
            }

            booking.reason?.let {
                item {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
            }
        }

        BookingPrimaryAction(
            booking = booking,
            isWorker = isWorker,
            onPay = onPay,
            viewModel = viewModel,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}

@Composable
fun BookingPrimaryAction(
    booking: BookingResponseDTO,
    isWorker: Boolean,
    onPay: (String) -> Unit,
    viewModel: BookingDetailViewModel,
    modifier: Modifier = Modifier
) {
    when (booking.state) {

        BookingState.WAITING_APPROVAL -> {
            if (isWorker) {
                Row(
                    modifier = modifier,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.acceptBooking(booking.id) }
                    ) { Text("Accept") }

                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.rejectBooking(booking.id) }
                    ) { Text("Reject") }
                }
            }
        }

        BookingState.PENDING -> {
            if (isWorker) {
                Button(
                    modifier = modifier.fillMaxWidth(),
                    onClick = { viewModel.startBooking(booking.id) }
                ) {
                    Text("Start work")
                }
            }
        }

        BookingState.PENDING_PAYMENT -> {
            if (!isWorker && booking.clientSecret != null) {
                Button(
                    modifier = modifier.fillMaxWidth(),
                    onClick = { onPay(booking.clientSecret) }
                ) {
                    Text("Pay now")
                }
            }
        }

        BookingState.IN_PROGRESS -> {
            if (isWorker) {
                Button(
                    modifier = modifier.fillMaxWidth(),
                    onClick = { viewModel.markFinishPending(booking.id) }
                ) {
                    Text("Mark as finished")
                }
            }
        }

        BookingState.FINISH_PENDING -> {
            if (!isWorker) {
                Column(
                    modifier = modifier,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { viewModel.confirmFinished(booking.id) }
                    ) {
                        Text("Confirm completion")
                    }

                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { viewModel.denyFinish(booking.id) }
                    ) {
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
    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Text(
            text = state.name,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}
