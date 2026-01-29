package com.moris.ergo.ui.widgets.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight

data class ErgoBottomBarAction(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit,
    val description: String? = null,
)

@Composable
fun ErgoBottomBar(navActions: List<ErgoBottomBarAction>, selected: Int) {
    NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
        repeat(navActions.size) { i ->
            NavigationBarItem(
                selected = selected == i,
                onClick = navActions[i].onClick,
                icon = {
                    Icon(
                        navActions[i].icon,
                        contentDescription = navActions[i].description,
                    )
                },
                label = {
                    Text(
                        text = navActions[i].label,
                        fontWeight = if (selected == i) FontWeight.Bold else null
                    )
                }
            )
        }
    }
}
