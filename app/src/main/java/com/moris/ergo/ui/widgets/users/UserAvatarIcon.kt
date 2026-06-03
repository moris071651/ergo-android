package com.moris.ergo.ui.widgets.users

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
fun UserAvatarIcon(
    pfpUrl: String?,
    modifier: Modifier = Modifier
) {
    if (!pfpUrl.isNullOrEmpty()) {
        SubcomposeAsyncImage(
            model = pfpUrl,
            contentDescription = "Profile picture",
            contentScale = ContentScale.Crop,
            modifier = modifier.clip(CircleShape),
            loading = { AvatarFallbackPlaceholder() },
            error = { AvatarFallbackPlaceholder() }
        )
    }
    else {
        AvatarFallbackPlaceholder(modifier = modifier)
    }
}

@Composable
private fun AvatarFallbackPlaceholder(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxSize(0.6f)
        )
    }
}
