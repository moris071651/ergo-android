package com.moris.ergo.ui.widgets.workers

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun YearsOfExperienceWidget(yearsOfExperience: Int) {
    val ending = if (yearsOfExperience != 1) "s" else ""

    Text(
        text = "$yearsOfExperience year$ending of experience",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.primary
    )
}
