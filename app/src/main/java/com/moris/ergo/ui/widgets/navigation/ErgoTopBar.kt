package com.moris.ergo.ui.widgets.navigation

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight

data class ErgoTopBarAction(
    val icon: ImageVector,
    val onClick: () -> Unit,
    val description: String? = null,
    val modifier: Modifier = Modifier
)

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ErgoTopBar(
    title: @Composable (() -> Unit),
    rightAction: ErgoTopBarAction? = null,
    leftAction: ErgoTopBarAction? = null
) {
    CenterAlignedTopAppBar(
        title = title,
        navigationIcon = {
            ErgoTopBarActionButton(rightAction)
        },
        actions = {
            ErgoTopBarActionButton(leftAction)
        }
    )
}

@Composable
fun ErgoTopBarActionButton(action: ErgoTopBarAction? = null) {
    if (action != null) {
        IconButton(
            onClick = action.onClick
        ) {
            Icon(
                imageVector = action.icon,
                modifier = action.modifier,
                contentDescription = action.description
            )
        }
    }
}
