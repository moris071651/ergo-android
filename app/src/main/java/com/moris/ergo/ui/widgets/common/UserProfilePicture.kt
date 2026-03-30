package com.moris.ergo.ui.widgets.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil3.compose.SubcomposeAsyncImage

@Composable
private fun DefaultProfileIcon() {
    Icon(
        imageVector = Icons.Default.Person,
        contentDescription = "Default profile icon",
        modifier = Modifier.fillMaxSize(0.6f),
        tint = MaterialTheme.colorScheme.onPrimaryContainer
    )
}

@Composable
fun UserProfilePicture(
    profileImageUrl: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        if (profileImageUrl.isNullOrBlank()) {
            DefaultProfileIcon()
        }
        else {
            SubcomposeAsyncImage(
                model = profileImageUrl,
                contentDescription = "Profile picture",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                loading = { DefaultProfileIcon() },
                error = { DefaultProfileIcon() }
            )
        }
    }
}
