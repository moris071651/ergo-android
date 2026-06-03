package com.moris.ergo.ui.screens.my_listings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.moris.ergo.TitleViewModel
import com.moris.ergo.data.dto.EditListingRequestDTO
import kotlin.math.ceil

@Composable
fun EditListingScreen(
    viewModel: MyListingsViewModel = hiltViewModel(),
    titleViewModel: TitleViewModel = hiltViewModel(),
    onSuccess: () -> Unit,
    listingId: String
) {
    LaunchedEffect(Unit) {
        titleViewModel.setTitle("Edit Listing")
        viewModel.loadEditableListing(listingId)
    }

    val editableListing by viewModel.editableListing.collectAsState()

    if (editableListing == null) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
    else {
        EditListingContent(
            editableListing = editableListing!!,
            onEditClick = { request ->
                viewModel.editListing(listingId, request, onSuccess = onSuccess)
            }
        )
    }

}

@Composable
fun EditListingContent(
    editableListing: MyListingItem,
    onEditClick: (EditListingRequestDTO) -> Unit
) {
    var title by remember { mutableStateOf(editableListing.title) }
    var description by remember { mutableStateOf(editableListing.description) }
    var price by remember { mutableStateOf((editableListing.priceCents!!.toDouble() / 100).toString()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Price (EUR)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
        }

        Surface(
            tonalElevation = 6.dp,
            shadowElevation = 10.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                enabled = title.isNotBlank() && price.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .height(52.dp),
                shape = MaterialTheme.shapes.large,
                onClick = {
                    onEditClick(
                        EditListingRequestDTO(
                            title = title,
                            description = description,
                            priceCents = ceil((price.toDoubleOrNull() ?: 0.0) * 100.0).toInt(),
                        )
                    )
                }
            ) {
                Text("Save Listing", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

//@Preview(showBackground = true, name = "Edit Service Listing Form Layout")
//@Composable
//fun EditListingFormPreview() {
//    MaterialTheme {
//        EditListingContent(
//            onEditClick = { _ -> Unit },
//            editableListing =
//        )
//    }
//}
