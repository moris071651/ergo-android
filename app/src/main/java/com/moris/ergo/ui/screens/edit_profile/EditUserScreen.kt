package com.moris.ergo.ui.screens.edit_profile

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moris.ergo.TitleViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun EditUserScreen(
    onNavigateBack: () -> Unit,
    editViewModel: EditProfileViewModel = hiltViewModel(),
    titleViewModel: TitleViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        titleViewModel.setTitle("Edit Personal Info")
        editViewModel.loadUserData()
    }

    val firstName by editViewModel.firstName.collectAsStateWithLifecycle()
    val lastName by editViewModel.lastName.collectAsStateWithLifecycle()
    val email by editViewModel.email.collectAsStateWithLifecycle()

    val isLoading by editViewModel.isLoading.collectAsStateWithLifecycle()
    val error by editViewModel.error.collectAsStateWithLifecycle()

    val context = LocalContext.current
    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            Log.d("hello2", it)
        }
    }

    LaunchedEffect(Unit) {
        editViewModel.uiEvent.collectLatest { event ->
            when (event) {
                is EditProfileViewModel.UiEvent.UserUpdateSuccess -> {
                    Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                    onNavigateBack()
                }
                is EditProfileViewModel.UiEvent.ImageUploadSuccess -> {
                    Toast.makeText(context, "Image uploaded!", Toast.LENGTH_SHORT).show()
                }
                else -> {

                }
            }
        }
    }

    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            val bytes = context.contentResolver.openInputStream(it)?.use { it.readBytes() }
            if (bytes != null) editViewModel.uploadImage(bytes)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = {
                pickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Change Profile Picture")
        }

        OutlinedTextField(
            value = firstName,
            onValueChange = editViewModel::onFirstNameChange,
            label = { Text("First Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = lastName ?: "",
            onValueChange = editViewModel::onLastNameChange,
            label = { Text("Last Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = email ?: "",
            onValueChange = editViewModel::onEmailChange,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { editViewModel.updateUserData() },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = MaterialTheme.shapes.large,
            enabled = !isLoading
        ) {
            if (isLoading) CircularProgressIndicator(Modifier.size(20.dp)) else Text("Save User Info")
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.5f),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}
