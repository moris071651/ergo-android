package com.moris.ergo.ui.screens.profile

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.moris.ergo.data.dto.UserLoginRequestDTO

@Composable
fun LoginScreen(
    onSuccess: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val error by viewModel.error.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    LoginScreenContent(
        onLoginClick = { email, password ->
            viewModel.login(
                UserLoginRequestDTO(email, password),
                onSuccess
            )
        },
        isLoading = isLoading
    )
}

@Composable
private fun LoginScreenContent(
    onLoginClick: (String, String) -> Unit,
    isLoading: Boolean
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
            .imePadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome Back",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "Enter your credentials to continue",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            placeholder = { Text(
                text = "name@example.com",
                color = Color.Gray.copy(alpha = 0.6f)
            ) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            shape = RoundedCornerShape(12.dp),
            trailingIcon = {
                val image =
                    if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = "Toggle password")
                }
            }
        )

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = { onLoginClick(email, password) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !isLoading,
            shape = RoundedCornerShape(12.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            }
            else {
                Text("Log In", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            LoginScreenContent(
                onLoginClick = { _, _ -> },
                isLoading = false,
            )
        }
    }
}
