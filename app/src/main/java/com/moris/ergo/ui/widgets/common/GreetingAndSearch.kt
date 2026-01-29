package com.moris.ergo.ui.widgets.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun GreetingAndSearch(name: String? = null, onSearchClick: () -> Unit) {
    val greeting = if (name != null) "Hi $name" else "Hi"

    val colors = TextFieldDefaults.colors()
    val backgroundColor = colors.unfocusedContainerColor
    val textColor = colors.unfocusedTextColor

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "$greeting, find your worker",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(
                    color = backgroundColor,
                    shape = MaterialTheme.shapes.medium
                )
                .clickable(onClick = onSearchClick),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = "Search for jobs, workers...",
                modifier = Modifier.padding(start = 16.dp),
                color = textColor
            )
        }
    }
}
