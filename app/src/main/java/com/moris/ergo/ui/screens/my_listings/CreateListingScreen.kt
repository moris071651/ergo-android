package com.moris.ergo.ui.screens.my_listings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.moris.ergo.data.api.CreateListingRequestDTO

@Composable
fun CreateListingScreen(
    viewModel: MyListingsViewModel = hiltViewModel(),
    onDone: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var recurring by remember { mutableStateOf(false) }
    var visitRequired by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TextField(title, { title = it }, label = { Text("Title") })
        TextField(description, { description = it }, label = { Text("Description") })
        TextField(price, { price = it }, label = { Text("Price (EUR cents)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        TextField(duration, { duration = it }, label = { Text("Duration (days)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(recurring, { recurring = it })
            Text("Allow recurring")
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(visitRequired, { visitRequired = it })
            Text("Visit required")
        }

        Button(
            enabled = title.isNotBlank() && price.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                viewModel.createListing(
                    CreateListingRequestDTO(
                        title = title,
                        description = description,
                        priceCents = price.toInt(),
                        allowRecurring = recurring,
                        visitRequired = visitRequired,
                        durationDays = duration.toInt(),
                    ),
                    onSuccess = onDone
                )
            }
        ) {
            Text("Create Listing")
        }
    }
}