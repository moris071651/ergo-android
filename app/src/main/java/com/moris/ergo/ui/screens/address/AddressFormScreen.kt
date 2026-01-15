package com.moris.ergo.ui.screens.address

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.moris.ergo.data.api.CreateAddressRequestDTO

@Composable
fun AddressFormScreen(
    addressId: String? = null,
    viewModel: AddressViewModel = hiltViewModel(),
    onComplete: () -> Unit
) {
    val selectedAddress by viewModel.selectedAddress.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var label by remember { mutableStateOf( selectedAddress?.label ?: "") }
    var lat by remember { mutableStateOf(selectedAddress?.lat?.toString() ?: "") }
    var lon by remember { mutableStateOf(selectedAddress?.lon?.toString() ?: "") }

    LaunchedEffect(addressId) {
        if (addressId != null) {
            viewModel.getAddressById(addressId)
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TextField(
            value = label,
            onValueChange = { label = it },
            label = { Text("Label") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = lat,
            onValueChange = { lat = it },
            label = { Text("Latitude") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        TextField(
            value = lon,
            onValueChange = { lon = it },
            label = { Text("Longitude") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                val address = CreateAddressRequestDTO(
                    lon = lon.toDouble(),
                    lat = lat.toDouble(),
                    label = label
                )

                if (addressId == null) {
                    viewModel.createAddress(address)
                }
                else {
                    viewModel.updateAddress(addressId, address)
                }

                onComplete()
            },
            enabled = label.isNotBlank() && lat.isNotBlank() && lon.isNotBlank() && !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (addressId == null) "Save Address" else "Update Address")
        }

        if (error != null) {
            Text("Error: $error", color = Color.Red)
        }

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}
