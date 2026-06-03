package com.moris.ergo

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.moris.ergo.ui.navigation.ErgoNavHost
import com.moris.ergo.ui.screens.profile.ProfileViewModel
import com.moris.ergo.ui.screens.search.SearchViewModel
import com.moris.ergo.ui.theme.ErgoTheme
import com.moris.ergo.ui.widgets.navigation.ErgoBottomBar
import com.moris.ergo.ui.widgets.navigation.ErgoBottomBarAction
import com.moris.ergo.ui.widgets.navigation.ErgoTopBar
import com.moris.ergo.ui.widgets.navigation.ErgoTopBarAction
import com.stripe.android.PaymentConfiguration
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

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
                    floatingActionButton = { FloatingActionButton(navController, currentRoute) },
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

@HiltViewModel
class TitleViewModel @Inject constructor() : ViewModel() {
    private val _title = MutableStateFlow<String?>(null)
    val title: StateFlow<String?> = _title

    fun setTitle(newTitle: String?) {
        _title.value = newTitle
    }
}

@Composable
fun TopBar(
    navController: NavHostController,
    currentRoute: String?
) {
    val title: String = currentRoute?.substringBefore("/")?.replaceFirstChar { it.uppercase() } ?: "Unknown"
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

    val backStackEntry by navController.currentBackStackEntryAsState()

    val titleViewModel = backStackEntry?.let { entry ->
        hiltViewModel<TitleViewModel>(entry)
    }

    val searchEntry = remember(backStackEntry) {
        try {
            navController.getBackStackEntry("search")
        }
        catch (e: Exception) {
            null
        }
    }

    val searchViewModel: SearchViewModel? = searchEntry?.let { hiltViewModel(it) }

    leftAction = ErgoTopBarAction(
        icon = Icons.Default.Search,
        modifier = Modifier.scale(scaleX = -1f, scaleY = 1f),
        onClick = {
            if (currentRoute == "search" && searchViewModel != null) searchViewModel.onSearchClick()
            else navController.navigate("search")
        }
    )

    val titleWidget = @Composable {
        if (currentRoute == "search" && searchViewModel != null) {
            val query by searchViewModel.query.collectAsState()

            TextField(
                value = query,
                onValueChange = searchViewModel::onQueryChange,
                placeholder = { Text("Search...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )
        }
        else {
            var screenTitle = ""
            if (titleViewModel != null) {
                val customTitle by titleViewModel.title.collectAsState()
                screenTitle = customTitle ?: title
            }
            else screenTitle = title

            Text(text = screenTitle, fontWeight = FontWeight.Bold)
        }
    }

    ErgoTopBar(
        title = titleWidget,
        rightAction = rightAction,
        leftAction = leftAction
    )
}

@Composable
fun FloatingActionButton(
    navController: NavHostController,
    currentRoute: String?
) {
    when {
        currentRoute?.equals("profile/address", true) ?: false -> {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate("profile/address/add") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp),
                icon = { Icon(Icons.Rounded.Add, "Add") },
                text = { Text("Add New") }
            )
        }
    }
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
