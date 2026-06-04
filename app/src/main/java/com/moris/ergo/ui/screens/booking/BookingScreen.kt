package com.moris.ergo.ui.screens.booking

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moris.ergo.ui.screens.profile.AddressDropdown
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.moris.ergo.data.dto.AddressDTO
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


@Composable
fun BookingScreen(
    listingId: String,
    onSuccess: () -> Unit,
    viewModel: BookingViewModel = hiltViewModel(),
) {
    val addresses by viewModel.addresses.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadAddresses()
    }

    BookingContent(
        addresses = addresses,
        isLoading = isLoading,
        error = error,
        onConfirmClick = { addressId, startAt, endAt ->
            viewModel.book(
                listingId = listingId,
                startAt = startAt,
                endAt = endAt,
                addressId = addressId,
                onSuccess = onSuccess
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingContent(
    addresses: List<AddressDTO>,
    isLoading: Boolean,
    error: String?,
    onConfirmClick: (addressId: String, startAt: String, endAt: String) -> Unit
) {
    var selectedAddressId by remember { mutableStateOf<String?>(null) }
    var startAt by remember { mutableStateOf("") }
    var endAt by remember { mutableStateOf("") }

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    val startDatePickerState = rememberDatePickerState()
    val endDatePickerState = rememberDatePickerState()

    val dateFormatter = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AddressDropdown(
                label = "Service Address",
                items = addresses,
                selectedId = selectedAddressId,
                onSelect = { selectedAddressId = it.id },
            )

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = startAt,
                    onValueChange = {},
                    label = { Text("Start Date") },
                    placeholder = { Text("Select date") },
                    trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    readOnly = true
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { showStartDatePicker = true }
                )
            }

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = endAt,
                    onValueChange = {},
                    label = { Text("End Date") },
                    placeholder = { Text("Select date") },
                    trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    readOnly = true
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { showEndDatePicker = true }
                )
            }

            if (error != null) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                enabled = !isLoading && selectedAddressId != null && startAt.isNotBlank() && endAt.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = MaterialTheme.shapes.large,
                onClick = {
                    onConfirmClick(selectedAddressId!!, startAt, endAt)
                }
            ) {
                Text("Confirm Booking", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (showStartDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showStartDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        startDatePickerState.selectedDateMillis?.let { millis ->
                            startAt = dateFormatter.format(Date(millis))
                        }
                        showStartDatePicker = false
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showStartDatePicker = false }) { Text("Cancel") }
                }
            ) {
                DatePicker(state = startDatePickerState)
            }
        }

        if (showEndDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showEndDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        endDatePickerState.selectedDateMillis?.let { millis ->
                            endAt = dateFormatter.format(Date(millis))
                        }
                        showEndDatePicker = false
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showEndDatePicker = false }) { Text("Cancel") }
                }
            ) {
                DatePicker(state = endDatePickerState)
            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(24.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 4.dp
                    )
                }
            }
        }
    }
}
@Preview(showBackground = true, name = "Booking Screen UI")
@Composable
fun FormPreview() {
    val mockAddresses = listOf(
        AddressDTO(
            id = "addr_01",
            lon = -73.935242,
            lat = 40.730610,
            label = "Home - New York",
            street = "Broadway",
            houseNumber = "1450",
            city = "New York",
            postalCode = "10001",
            country = "United States"
        ),
        AddressDTO(
            id = "addr_02",
            lon = -118.243685,
            lat = 34.052234,
            label = "Office - Los Angeles",
            street = "Hope St",
            houseNumber = "601",
            city = "Los Angeles",
            postalCode = "90071",
            country = "United States"
        )
    )

    MaterialTheme {
        BookingContent(
            addresses = mockAddresses,
            isLoading = false,
            error = null,
            onConfirmClick = { _, _, _ -> }
        )
    }
}

@Preview(showBackground = true, name = "Booking Screen Loading State")
@Composable
fun LoadingFormPreview() {
    MaterialTheme {
        BookingContent(
            addresses = emptyList(),
            isLoading = true,
            error = null,
            onConfirmClick = { _, _, _ -> }
        )
    }
}
