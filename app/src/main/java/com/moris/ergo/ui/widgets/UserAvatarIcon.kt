package com.moris.ergo.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color

@Composable
fun UserAvatarIcon(pfpUrl: String?, modifier: Modifier) {
    if (!pfpUrl.isNullOrEmpty()) {
        Box(
            modifier = modifier
                .clip(CircleShape)
                .background(Color.Gray)
        )
    } else {
        Box(
            modifier = modifier
                .clip(CircleShape)
                .background(Color.Gray)
        )
    }
}
