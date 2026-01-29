package com.moris.ergo.ui.widgets.workers

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.moris.ergo.data.scheme.WorkerSummaryInfo
import com.moris.ergo.ui.widgets.users.StarRatingWidget
import com.moris.ergo.ui.widgets.users.UserAvatarIcon

@Composable
fun WorkerSmallCard(
    worker: WorkerSummaryInfo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 14.dp,
                vertical = 4.dp
            )
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Spacer(Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        UserAvatarIcon(worker.pfpUrl, Modifier.size(48.dp))

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = worker.name,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = worker.skill,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    YearsOfExperienceWidget(worker.yearsOfExperience)
                }

                Spacer(modifier = Modifier.weight(1f))

                StarRatingWidget(worker.rating)

                Spacer(Modifier.width(4.dp))
            }
        }
    }
}
