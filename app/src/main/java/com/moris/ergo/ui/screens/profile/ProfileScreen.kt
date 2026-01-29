package com.moris.ergo.ui.screens.profile

import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.moris.ergo.data.dto.CurrentUserResponseDTO
import com.moris.ergo.data.dto.CurrentWorkerResponseDTO
import androidx.core.net.toUri

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onLoginClick: () -> Unit,
    onSignupClick: () -> Unit,
    onMyListingsClick: () -> Unit,
    onAddressButtonClick: () -> Unit,
    onBecomeWorkerClick: () -> Unit
) {
    val context = LocalContext.current
    val user by viewModel.user.collectAsState()
    val worker by viewModel.worker.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        try {
            viewModel.loadSession()
        } catch (e: Exception) {
            Log.d("DEBUG", e.toString())
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator()
            }

            user == null -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(onClick = onLoginClick) {
                        Text("Login")
                    }
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = onSignupClick) {
                        Text("Sign Up")
                    }
                    error?.let {
                        Spacer(Modifier.height(16.dp))
                        Text(it, color = Color.Red)
                    }
                }
            }

            else -> {
                ProfileContent(
                    user = user!!,
                    worker = worker,
                    onAddressButtonClick = onAddressButtonClick,
                    onBecomeWorkerClick = onBecomeWorkerClick,
                    onLogout = { viewModel.logout {} },
                    onMyListingsClick = onMyListingsClick,
                    startStripeOnboarding = {
                        viewModel.startStripeOnboarding { url ->
                            val intent = CustomTabsIntent.Builder().build()
                            intent.launchUrl(context, url.toUri())
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun ProfileContent(
    user: CurrentUserResponseDTO,
    worker: CurrentWorkerResponseDTO?,
    onAddressButtonClick: () -> Unit,
    onBecomeWorkerClick: () -> Unit,
    onLogout: () -> Unit,
    onMyListingsClick: () -> Unit,
    startStripeOnboarding: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Profile", style = MaterialTheme.typography.headlineMedium)

        Card {
            Column(Modifier.padding(12.dp)) {
                Text("${user.firstName} ${user.lastName}", fontWeight = FontWeight.Bold)
                Text(user.email)
                Text("Joined: ${user.createdAt}")
            }
        }

        Button(onClick = onAddressButtonClick) {
            Text("Your addresses")
        }

        if (worker != null) {
            WorkerInfoCard(worker, startStripeOnboarding)
            if (worker.payoutsEnabled && worker.chargesEnabled) {
                Button(onClick = onMyListingsClick) {
                    Text("My Listings")
                }
            }
        }
        else {
            Button(
                onClick = onBecomeWorkerClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Become a Worker")
            }
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("Logout", color = Color.White)
        }
    }
}

@Composable
private fun WorkerInfoCard(
    worker: CurrentWorkerResponseDTO,
    startStripeOnboarding: () -> Unit
) {
    Card {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text("Worker Profile", style = MaterialTheme.typography.titleMedium)

            Text("Bio: ${worker.bio}")
            Text("Experience: ${worker.experienceYears} years")
            Text("Service radius: ${worker.serviceRadiusKm} km")
            Text("Skills: ${worker.skills.joinToString()}")

            Text(
                if (worker.available) "Available" else "Unavailable",
                color = if (worker.available) Color.Green else Color.Gray
            )

            if (!worker.payoutsEnabled && !worker.chargesEnabled) {
                Text(
                    "Payments not fully set up",
                    color = Color(0xFFFF9800)
                )

                Button(onClick = startStripeOnboarding) {
                    Text("Setup Payments")
                }
            }
        }
    }
}
