package com.moris.ergo.ui.screens.worker_detail

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.moris.ergo.TitleViewModel
import com.moris.ergo.data.dto.UserResponseDTO
import com.moris.ergo.data.dto.WorkerResponseDTO
import com.moris.ergo.data.scheme.ListingBriefInfo
import com.moris.ergo.ui.widgets.listings.ListingBriefInfoCarousel
import com.moris.ergo.ui.widgets.common.SectionHeader
import com.moris.ergo.ui.widgets.users.UserAvatarIcon

@Composable
fun WorkerDetailScreen(
    userId: String,
    onListingClick: (String) -> Unit,
    onContactClick: (String, Context) -> Unit,
    titleViewModel: TitleViewModel = hiltViewModel(),
    viewModel: WorkerDetailViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()
    val worker by viewModel.worker.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val listings by viewModel.listings.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(userId) {
        titleViewModel.setTitle("Worker Detail")
        viewModel.loadWorker(userId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            worker != null && user != null -> {
                WorkerDetailContent(
                    user = user!!,
                    worker = worker!!,
                    listings = listings,
                    onListingClick = onListingClick,
                    onContactClick = { onContactClick(user!!.email, context) }
                )
            }
        }
    }
}

@Composable
fun WorkerDetailContent(
    user: UserResponseDTO,
    worker: WorkerResponseDTO,
    listings: List<ListingBriefInfo>,
    onListingClick: (String) -> Unit,
    onContactClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f).padding(top = 16.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                WorkerHeaderSection(
                    profileImageUrl = user.profileImageUrl,
                    fullName = "${user.firstName} ${user.lastName}",
                    primarySkill = worker.skills.firstOrNull() ?: "General Worker"
                )
            }

            item {
                WorkerAboutSection(bio = worker.bio)
            }

            item {
                WorkerStatsCard(
                    experienceYears = worker.experienceYears,
                    serviceRadiusKm = worker.serviceRadiusKm,
                    isAvailable = worker.available
                )
            }

            item {
                WorkerContactSection(email = user.email, onContactClick = onContactClick)
            }

            item {
                SectionHeader("Worker's Listings")

                if (listings.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        ListingBriefInfoCarousel(listings, onListingClick)
                    }
                }
                else {
                    NoListingsAvailable()
                }
            }
        }
    }
}

@Composable
fun WorkerHeaderSection(
    profileImageUrl: String?,
    fullName: String,
    primarySkill: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        UserAvatarIcon(
            pfpUrl = profileImageUrl,
            modifier = Modifier.size(100.dp)
        )

        Text(
            text = fullName,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = primarySkill,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun WorkerAboutSection(bio: String) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = "About",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = bio,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 22.sp
        )
    }
}

@Composable
fun WorkerStatsCard(
    experienceYears: Int,
    serviceRadiusKm: Int,
    isAvailable: Boolean
) {
    Card(
        modifier = Modifier.padding(horizontal = 20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Experience", style = MaterialTheme.typography.bodyMedium)
                Text("$experienceYears Year${if (experienceYears > 1) "s" else ""}", fontWeight = FontWeight.Bold)
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Service Radius", style = MaterialTheme.typography.bodyMedium)
                Text("$serviceRadiusKm km", fontWeight = FontWeight.Bold)
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Availability Status", style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = if (isAvailable) "Available for work" else "Currently unavailable",
                    fontWeight = FontWeight.Bold,
                    color = if (isAvailable) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun WorkerContactSection(
    email: String,
    onContactClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "Contact Information",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = email,
            modifier = Modifier.clickable() { onContactClick() },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun NoListingsAvailable(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp)
    ) {
        Text(
            text = "This worker hasn't published any active service listings yet.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true, name = "Worker Profile Dashboard")
@Composable
fun WorkerDetailPreview() {
    val mockUser = UserResponseDTO(
        "whoknows", "marcus@ergo-app.com", "",
        firstName = "Marcus",
        lastName = "Aurelius",
        createdAt = "2025-01-10"
    )
    val mockWorker = WorkerResponseDTO(
        bio = "Professional carpentry installations, custom kitchen designs, fine finish woodworking repairs, and restoration work with over 8 years in field contract projects.",
        skills = listOf("Master Carpentry", "Wood Finish"),
        experienceYears = 1,
        serviceRadiusKm = 25,
        available = true,
        userId = "whoknows",
        createdAt = "2025-01-10"
    )
    val mockListings = listOf(ListingBriefInfo(
            "1", "Cabinet Build",
            rating = 1.2,
            priceString = "3$",
            description = "TODO()",
            primaryImageUrl = ""
        ),
        ListingBriefInfo(
            "2", "Table Sanding",
            rating = 1.2,
            priceString = "3$",
            description = "TODO()",
            primaryImageUrl = ""
    ))

    MaterialTheme {
        WorkerDetailContent(
            user = mockUser,
            worker = mockWorker,
            listings = listOf(),
            onListingClick = {},
            onContactClick = {}
        )
    }
}




