package com.moris.ergo.ui.screens.booking

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.moris.ergo.ui.screens.profile.AddressDropdown

@Composable
fun BookingScreen(
    listingId: String,
    viewModel: BookingViewModel = hiltViewModel(),
    onSuccess: () -> Unit
) {
    val addresses by viewModel.addresses.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadAddresses()
    }

    var selectedAddressId by remember { mutableStateOf<String?>(null) }
    var startAt by remember { mutableStateOf("") }
    var endAt by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text("Book this service", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        AddressDropdown(
            label = "Service Address",
            items = addresses,
            selectedId = addresses.firstOrNull { it.id == selectedAddressId }?.label,
            onSelect = { selectedAddressId = it.id },
        )

        TextField(
            value = startAt,
            onValueChange = { startAt = it },
            label = { Text("Start date (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = endAt,
            onValueChange = { endAt = it },
            label = { Text("End date (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.weight(1f))

        Button(
            enabled = !isLoading && selectedAddressId != null && startAt.isNotBlank() && endAt.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                viewModel.book(
                    listingId = listingId,
                    startAt = startAt,
                    endAt = endAt,
                    addressId = selectedAddressId!!,
                    onSuccess = onSuccess
                )
            }
        ) {
            Text("Confirm Booking")
        }

        if (error != null) {
            Text(error!!, color = Color.Red)
        }
    }
}
