package com.moris.ergo.ui.screens.edit_profile

import android.util.MutableInt
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Radar
import androidx.compose.material.icons.rounded.WorkHistory
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moris.ergo.TitleViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.roundToInt

@Composable
fun EditWorkerScreen(
    onNavigateBack: () -> Unit,
    editViewModel: EditProfileViewModel = hiltViewModel(),
    titleViewModel: TitleViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val bio by editViewModel.bio.collectAsStateWithLifecycle()
    val serviceRadius by editViewModel.serviceRadius.collectAsStateWithLifecycle()
    val experienceYears by editViewModel.experienceYears.collectAsStateWithLifecycle()
    val isLoading by editViewModel.isLoading.collectAsStateWithLifecycle()
    val error by editViewModel.error.collectAsStateWithLifecycle()
    var invalidInputExp by remember { mutableStateOf(false) }
    var invalidInputRad by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        titleViewModel.setTitle("Edit Worker Info")
        editViewModel.loadWorkerData()
    }

    LaunchedEffect(Unit) {
        editViewModel.uiEvent.collectLatest { event ->
            when (event) {
                is EditProfileViewModel.UiEvent.WorkerUpdateSuccess -> {
                    Toast.makeText(context, "Worker profile updated!", Toast.LENGTH_SHORT).show()
                    onNavigateBack()
                }
                else -> {

                }
            }
        }
    }

    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = bio,
                onValueChange = editViewModel::onBioChange,
                label = { Text("Professional Bio") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 6,
                enabled = !isLoading
            )

            OutlinedTextField(
                value = if (experienceYears == 0) "" else experienceYears.toString(),
                onValueChange = { input ->
                    val cleanInput = input.filter { it.isDigit() }

                    if (cleanInput.isEmpty()) {
                        editViewModel.onExperienceChange(0)
                    }
                    else {
                        invalidInputExp = false
                        val num = cleanInput.toIntOrNull()
                        if (num != null && num in 1..99) {
                            input.toIntOrNull()?.let { editViewModel.onExperienceChange(it) }
                        }
                        else {
                            invalidInputExp = true
                            Toast.makeText(
                                context,
                                "Experience must be between 1 and 99 years",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                },
                label = { Text("Exp (years)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = { Icon(Icons.Rounded.WorkHistory, null, modifier = Modifier.size(18.dp)) }
            )

            OutlinedTextField(
                value = if (serviceRadius == 0) "" else serviceRadius.toString(),
                onValueChange = { input ->
                    val cleanInput = input.filter { it.isDigit() }

                    if (cleanInput.isEmpty()) {
                        editViewModel.onRadiusChange(0)
                    }
                    else {
                        invalidInputRad = false
                        val num = cleanInput.toIntOrNull()
                        if (num != null && num in 1..99) {

                            input.toIntOrNull()?.let { editViewModel.onRadiusChange(it) }
                        }
                        else {
                            invalidInputRad = true
                            Toast.makeText(
                                context,
                                "Radius must be between 1 and 99 km",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    input.toIntOrNull()?.let { editViewModel.onRadiusChange(it) }
                                },
                label = { Text("Rad (km)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = { Icon(Icons.Rounded.Radar, null, modifier = Modifier.size(18.dp)) }
            )


            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { editViewModel.updateWorkerData() },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = MaterialTheme.shapes.large,
                enabled = !isLoading && !invalidInputExp && !invalidInputRad
            ) {
                Text("Save Worker Info")
            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}
