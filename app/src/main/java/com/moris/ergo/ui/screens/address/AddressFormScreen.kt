package com.moris.ergo.ui.screens.address

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Label
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.moris.ergo.TitleViewModel
import com.moris.ergo.data.dto.CreateAddressRequestDTO
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import java.io.File

@Composable
fun AddressFormScreen(
    addressId: String? = null,
    titleViewModel: TitleViewModel = hiltViewModel(),
    viewModel: AddressViewModel = hiltViewModel(),
    onComplete: () -> Unit
) {
    if (addressId == null) titleViewModel.setTitle("Add address")
    else titleViewModel.setTitle("Edit address")

    val selectedAddress by viewModel.selectedAddress.collectAsState()
    var isReady by remember { mutableStateOf(false) }
    val error by viewModel.error.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    var label by remember { mutableStateOf("") }
    var lat by remember { mutableStateOf("42.6977") }
    var lon by remember { mutableStateOf("23.3219") }

    LaunchedEffect(addressId) {
        if (addressId != null) {
            viewModel.getAddressById(addressId)
        }
        else {
            isReady = true
        }
    }

    LaunchedEffect(selectedAddress) {
        selectedAddress?.let {
            label = it.label
            lat = it.lat.toString()
            lon = it.lon.toString()
            isReady = true
        }
    }

    if (!isReady) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary
            )
        }

        return
    }

    val mapView = remember {
        MapView(context).apply {
            Configuration.getInstance().userAgentValue = context.packageName
            Configuration.getInstance().osmdroidTileCache = File(context.cacheDir, "osmdroid")
            setMultiTouchControls(true)
            isTilesScaledToDpi = true
            controller.setZoom(16.0)
            controller.setCenter(GeoPoint(lat.toDouble(), lon.toDouble()))
            setBuiltInZoomControls(false)
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            val center = mapView.mapCenter as GeoPoint
            lat = String.format("%.6f", center.latitude)
            lon = String.format("%.6f", center.longitude)
            kotlinx.coroutines.delay(150)
        }
    }

    val innerSpacing = 16.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = innerSpacing)
            .padding(horizontal = innerSpacing)
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(innerSpacing)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth().weight(1f),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AndroidView(
                    factory = { mapView },
                    modifier = Modifier.fillMaxSize()
                )

                Icon(
                    imageVector = Icons.Rounded.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(44.dp)
                        .align(Alignment.Center)
                        .graphicsLayer(translationY = -60f)
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(innerSpacing)
        ) {
            OutlinedTextField(
                value = label,
                onValueChange = { label = it },
                label = { Text("Address Name") },
                placeholder = { Text("Home, Office, etc.") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Label,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(innerSpacing)
            ) {
                CoordinateDisplay(
                    modifier = Modifier.weight(1f),
                    label = "Latitude",
                    value = lat
                )
                CoordinateDisplay(
                    modifier = Modifier.weight(1f),
                    label = "Longitude",
                    value = lon
                )
            }

            Button(
                onClick = {
                    val address = CreateAddressRequestDTO(
                        label = label,
                        lat = lat.toDouble(),
                        lon = lon.toDouble()
                    )

                    if (addressId == null) viewModel.createAddress(address)
                    else viewModel.updateAddress(addressId, address)
                    onComplete()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = label.isNotBlank()
            ) {
                Icon(Icons.Rounded.Check, null)
                Spacer(Modifier.width(8.dp))
                Text("Confirm Address", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
fun CoordinateDisplay(modifier: Modifier, label: String, value: String) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall)
            Text(
                value,
                style = MaterialTheme.typography.bodyLarge,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
