package com.moris.ergo.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.moris.ergo.data.dto.AddressDTO
import com.moris.ergo.data.dto.BecomeWorkerRequestDTO

@Composable
fun BecomeWorkerScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onSuccess: () -> Unit
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val addresses by viewModel.addresses.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadUserAddresses()
    }

    var selectedAddressId by remember { mutableStateOf<String?>(null) }
    var bio by remember { mutableStateOf("") }
    var radius by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }
    var skills by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        AddressDropdown(
            label = "Main Address",
            items = addresses,
            selectedId = selectedAddressId,
            onSelect = { selectedAddressId = it.id }
        )

        TextField(
            value = bio,
            onValueChange = { bio = it },
            label = { Text("Bio") }
        )

        TextField(
            value = radius,
            onValueChange = { radius = it },
            label = { Text("Service radius (km)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        TextField(
            value = experience,
            onValueChange = { experience = it },
            label = { Text("Experience (years)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        TextField(
            value = skills,
            onValueChange = { skills = it },
            label = { Text("Skills (comma separated)") }
        )

        Button(
            enabled = !isLoading && selectedAddressId != null,
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                viewModel.becomeWorker(
                    BecomeWorkerRequestDTO(
                        addressId = selectedAddressId!!,
                        bio = bio,
                        serviceRadiusKm = radius.toInt(),
                        experienceYears = experience.toInt(),
                        skills = skills.split(",").map { it.trim() }
                    ),
                    onSuccess = onSuccess
                )
            }
        ) {
            Text("Activate Worker Account")
        }

        if (error != null) {
            Text(error!!, color = Color.Red)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressDropdown(
    label: String,
    items: List<AddressDTO>,
    selectedId: String?,
    onSelect: (AddressDTO) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val selectedItem = items.find { it.id == selectedId }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            value = selectedItem?.label ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { address ->
                DropdownMenuItem(
                    text = {
                        Text("${address.label} • ${address.city}")
                    },
                    onClick = {
                        onSelect(address)
                        expanded = false
                    }
                )
            }
        }
    }
}
