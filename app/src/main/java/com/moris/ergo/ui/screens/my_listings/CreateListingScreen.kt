package com.moris.ergo.ui.screens.my_listings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.moris.ergo.data.dto.CreateListingRequestDTO
import kotlin.math.ceil

@Composable
fun CreateListingScreen(
    viewModel: MyListingsViewModel = hiltViewModel(),
    onDone: () -> Unit
) {
    CreateListingContent(
        onCreateClick = { request, imageUris ->
            viewModel.createListingWithImages(request, imageUris, onSuccess = onDone)
        }
    )
}

@Composable
fun CreateListingContent(
    onCreateClick: (CreateListingRequestDTO, List<Uri>) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var visitRequired by remember { mutableStateOf(false) }

    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.isNotEmpty()) {
            imageUris = imageUris + uris
        }
    }

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
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Service Images",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { imagePickerLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Pick images",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    items(imageUris) { uri ->
                        ImageThumbnailCard(
                            uri = uri,
                            onRemoveClick = { imageUris = imageUris - uri }
                        )
                    }
                }
            }

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

            OutlinedTextField(
                value = duration,
                onValueChange = { duration = it },
                label = { Text("Duration (days)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Checkbox(
                    checked = visitRequired,
                    onCheckedChange = { visitRequired = it }
                )
                Text(
                    text = "Pre-visit assessment required",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
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
                    onCreateClick(
                        CreateListingRequestDTO(
                            title = title,
                            description = description,
                            priceCents = ceil((price.toDoubleOrNull() ?: 0.0) * 100.0).toInt(),
                            allowRecurring = false,
                            visitRequired = visitRequired,
                            durationDays = duration.toIntOrNull() ?: 1
                        ),
                        imageUris
                    )
                }
            ) {
                Text("Create Listing", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ImageThumbnailCard(
    uri: Uri,
    onRemoveClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(90.dp)
            .clip(MaterialTheme.shapes.medium)
    ) {
        AsyncImage(
            model = uri,
            contentDescription = "Selected picture preview",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .size(20.dp)
                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                .clickable { onRemoveClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove asset image",
                tint = Color.White,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

@Preview(showBackground = true, name = "Create Service Listing Form Layout")
@Composable
fun CreateListingFormPreview() {
    MaterialTheme {
        CreateListingContent(
            onCreateClick = {_, _ -> Unit }
        )
    }
}
