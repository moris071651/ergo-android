package com.moris.ergo.ui.screens.worker_detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.moris.ergo.data.dto.UserResponseDTO
import com.moris.ergo.data.dto.WorkerResponseDTO
import com.moris.ergo.data.scheme.ListingBriefInfo
import com.moris.ergo.ui.widgets.listings.ListingBriefInfoCarousel
import com.moris.ergo.ui.widgets.common.SectionHeader
import com.moris.ergo.ui.widgets.users.StarRatingWidget
import com.moris.ergo.ui.widgets.users.UserAvatarIcon

@Composable
fun WorkerDetailScreen(
    userId: String,
    onListingClick: (String) -> Unit,
    onContactClick: () -> Unit,
    viewModel: WorkerDetailViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()
    val worker by viewModel.worker.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val listings by viewModel.listings.collectAsState()

    LaunchedEffect(userId) {
        viewModel.loadWorker(userId)
    }

    when {
        isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        worker != null && user != null -> {
            WorkerDetailContent(
                user = user!!,
                worker = worker!!,
                listings = listings,
                onListingClick = onListingClick,
                onContactClick = onContactClick
            )
        }
    }
}

@Composable
fun WorkerDetailContent(
    user: UserResponseDTO,
    worker: WorkerResponseDTO,
    listings: List<ListingBriefInfo>,
    onListingClick:  (String) -> Unit,
    onContactClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 72.dp)
        ) {

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    UserAvatarIcon(
                        user.profileImageUrl,
                        modifier = Modifier.size(96.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "${user.firstName} ${user.lastName}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = worker.skills.firstOrNull() ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    StarRatingWidget(3.0)
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "About",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = worker.bio,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Years of experience: ${worker.experienceYears}",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Service radius: ${worker.serviceRadiusKm} km",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (worker.available) "Available for work"
                        else "Currently unavailable",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (worker.available)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.error
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "Contact",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = user.email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                SectionHeader("Worker's Listings")
                ListingBriefInfoCarousel(listings, onListingClick)
            }

        }

        Button(
            onClick = onContactClick,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Contact Worker")
        }
    }
}
