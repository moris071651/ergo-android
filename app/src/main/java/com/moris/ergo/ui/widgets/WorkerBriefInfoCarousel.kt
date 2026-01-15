package com.moris.ergo.ui.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.moris.ergo.data.scheme.WorkerBriefInfo

@Composable
fun WorkerBriefInfoCarousel(
    workers: List<WorkerBriefInfo>,
    onClick: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(workers) { worker ->
            WorkerBriefInfoCard(
                worker = worker,
                onClick = { onClick(worker.userId) }
            )
        }
    }
}
