package com.moris.ergo.ui.screens.profile

import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.moris.ergo.data.dto.CurrentUserResponseDTO
import com.moris.ergo.data.dto.CurrentWorkerResponseDTO
import androidx.core.net.toUri
import com.moris.ergo.ui.widgets.users.UserAvatarIcon

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onLoginClick: () -> Unit,
    onSignupClick: () -> Unit,
    onMyPersonalInfoClick: () -> Unit,
    onMyWorkerInfoClick: () -> Unit,
    onMyAddressesClick: () -> Unit,
    onMyListingsClick: () -> Unit,
    onBecomeWorkerClick: () -> Unit,
) {
    val context = LocalContext.current
    val user by viewModel.user.collectAsState()
    val worker by viewModel.worker.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        try {
            viewModel.loadSession()
        }
        catch (e: Exception) {
            Log.d("DEBUG", e.toString())
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator()
            }

            user == null -> {
                GuestProfileContent(onLoginClick, onSignupClick)
            }

            else -> {
                val startStripeOnboarding = {
                    viewModel.startStripeOnboarding { url ->
                        val intent = CustomTabsIntent.Builder().build()
                        intent.launchUrl(context, url.toUri())
                    }
                }

                ProfileContent(
                    user = user!!,
                    worker = worker,
                    onMyPersonalInfoClick = onMyPersonalInfoClick,
                    onMyWorkerInfoClick = onMyWorkerInfoClick,
                    onMyAddressesClick = onMyAddressesClick,
                    onMyListingsClick = onMyListingsClick,
                    onBecomeWorkerClick = onBecomeWorkerClick,
                    startStripeOnboarding = startStripeOnboarding,
                    onLogout = { viewModel.logout {} }
                )
            }
        }
    }
}

@Composable
private fun GuestProfileContent(
    onLoginClick: () -> Unit,
    onSignupClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(120.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier.padding(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(Modifier.height(32.dp))

        Text(
            text = "Your Profile",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Log in to manage your addresses, view your worker profile, and track your listings.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Spacer(Modifier.height(40.dp))

        Button(
            onClick = onSignupClick,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Create Account", style = MaterialTheme.typography.titleMedium)
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = onLoginClick,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Log In", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun ProfileContent(
    user: CurrentUserResponseDTO,
    worker: CurrentWorkerResponseDTO?,
    onMyPersonalInfoClick: () -> Unit,
    onMyWorkerInfoClick: () -> Unit,
    onMyAddressesClick: () -> Unit,
    onMyListingsClick: () -> Unit,
    onBecomeWorkerClick: () -> Unit,
    startStripeOnboarding: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            UserAvatarIcon(
                pfpUrl = user.profileImageUrl,
                modifier = Modifier.size(110.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "${user.firstName} ${user.lastName}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = user.email,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (worker != null) {
            SectionHeader("Worker Profile")

            Text(
                text = worker.bio,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                WorkerStatColumn("Experience", "${worker.experienceYears}y")
                WorkerStatColumn("Radius", "${worker.serviceRadiusKm}km")
                WorkerStatColumn("Status", if (worker.available) "Available" else "Busy",
                    color = if (worker.available) Color(0xFF2E7D32) else Color.Gray)
            }

            if (!worker.payoutsEnabled || !worker.chargesEnabled) {
                ProfileMenuItem(
                    Icons.Default.Warning,
                    label = "Finish payment setup to accept jobs",
                    onClick = startStripeOnboarding,
                    highlight = true
                )
            }

            Spacer(Modifier.height(12.dp))
        }

        SectionHeader("Account Settings")

        ProfileMenuItem(Icons.Default.Person, "My Personal Info", onMyPersonalInfoClick)
        if (worker != null && worker.payoutsEnabled) {
            ProfileMenuItem(Icons.Default.Agriculture, "My Worker Info", onMyWorkerInfoClick)
        }

        ProfileMenuItem(Icons.Default.LocationOn, "My Addresses", onMyAddressesClick)
        if (worker != null && worker.payoutsEnabled) {
            ProfileMenuItem(Icons.Default.List, "My Listings", onMyListingsClick)
        }

        if (worker == null) {
            ProfileMenuItem(Icons.Default.AddCircle, "Become a Worker", onBecomeWorkerClick)
        }
        ProfileMenuItem(Icons.Default.ExitToApp, "Logout", onLogout)
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun WorkerStatColumn(label: String, value: String, color: Color = Color.Unspecified) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    highlight: Boolean = false
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = if (highlight) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f) else Color.Transparent
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = if (highlight) MaterialTheme.colorScheme.error else LocalContentColor.current
            )

            Spacer(Modifier.width(16.dp))
            Text(label, style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.weight(1f))

            Icon(Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = if (highlight) LocalContentColor.current else Color.Gray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewProfileContent() {
    val mockUser = CurrentUserResponseDTO(
        firstName = "John",
        lastName = "Doe",
        email = "john.doe@example.com",
        createdAt = "2023-10-01",
                updatedAt = "",
        profileImageUrl = "",
        id = "hello"
    )

    val mockWorker = CurrentWorkerResponseDTO(
        userId= "hello",
        bio = "Experienced plumber available for hire. Experienced plumber available for hire. Experienced plumber available for hire. Experienced plumber available for hire. Experienced plumber available for hire.",
        serviceRadiusKm = 5,
        experienceYears = 20,
        skills = listOf("Plumber", "Electrical"),
        available = true,
        createdAt = "2023-10-01",
        updatedAt = "2023-10-01",
        stripeAccountId = "",
        chargesEnabled = false,
        payoutsEnabled = false
    )

    MaterialTheme {
        ProfileContent(
            user = mockUser,
            worker = null,
            onMyPersonalInfoClick = {},
            onMyWorkerInfoClick = {},
            onMyAddressesClick = {},
            onMyListingsClick = {},
            onBecomeWorkerClick = {},
            startStripeOnboarding = {},
            onLogout = {}
        )
    }
}
