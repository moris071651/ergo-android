package com.moris.ergo

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.moris.ergo.ui.navigation.ErgoNavHost
import com.moris.ergo.ui.screens.profile.ProfileViewModel
import com.moris.ergo.ui.theme.ErgoTheme
import com.moris.ergo.ui.widgets.navigation.ErgoBottomBar
import com.moris.ergo.ui.widgets.navigation.ErgoBottomBarAction
import com.moris.ergo.ui.widgets.navigation.ErgoTopBar
import com.moris.ergo.ui.widgets.navigation.ErgoTopBarAction
import com.stripe.android.PaymentConfiguration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: ProfileViewModel by viewModels()

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PaymentConfiguration.init(
            applicationContext,
            BuildConfig.STRIPE_PUBLISHABLE_KEY
        )

        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = (navBackStackEntry?.destination?.route)?.lowercase()?.trim()

            ErgoTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { TopBar(navController, currentRoute) },
                    bottomBar = { BottomBar(navController, currentRoute) }
                ) { innerPadding ->
                    ErgoNavHost(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    private fun handleDeepLink(intent: Intent?) {
        intent?.data?.let { uri ->
            if (uri.host == "stripe") {
                viewModel.refreshWorkerStatus()
            }
        }
    }
}

@Composable
fun TopBar(
    navController: NavHostController,
    currentRoute: String?
) {
    val title: String = currentRoute?.replaceFirstChar { it.uppercase() } ?: "Unknown"
    var leftAction: ErgoTopBarAction? = null
    var rightAction: ErgoTopBarAction? = null

    if (currentRoute == null) {
        rightAction = ErgoTopBarAction(
            icon = Icons.Default.Home,
            onClick = { navController.navigate("home") }
        )
    } else if (currentRoute != "home") {
        rightAction = ErgoTopBarAction(
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            onClick = { navController.popBackStack() }
        )
    }

    if (currentRoute != "search") {
        leftAction = ErgoTopBarAction(
            icon = Icons.Default.Search,
            onClick = { navController.navigate("search") }
        )
    }

    ErgoTopBar(
        title = title,
        rightAction = rightAction,
        leftAction = leftAction
    )
}

@Composable
fun BottomBar(
    navController: NavHostController,
    currentRoute: String?
) {
    var selectedOption = 0
    val navActions = listOf(
        ErgoBottomBarAction(
            label = "Home",
            icon = Icons.Default.Home,
            onClick = { navController.navigate("home") }
        ),
        ErgoBottomBarAction(
            label = "Favorites",
            icon = Icons.Default.Favorite,
            onClick = { navController.navigate("favorites") }
        ),
        ErgoBottomBarAction(
            label = "Ongoing",
            icon = Icons.Default.Build,
            onClick = { navController.navigate("ongoing") }
        ),
        ErgoBottomBarAction(
            label = "Profile",
            icon = Icons.Default.AccountCircle,
            onClick = { navController.navigate("profile") }
        )
    )

    var showBar = false
    for ((index, action) in navActions.withIndex()) {
        val label = action.label.lowercase().trim()
        if (currentRoute?.startsWith(label) == true) {
            selectedOption = index
            showBar = true
        }
    }

    if (showBar) {
        ErgoBottomBar(
            selected = selectedOption,
            navActions = navActions
        )
    }
}
