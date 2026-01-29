package com.moris.ergo.ui.navigation

import com.moris.ergo.ui.screens.home.HomeScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.moris.ergo.ui.screens.address.AddressFormScreen
import com.moris.ergo.ui.screens.address.AddressListScreen
import com.moris.ergo.ui.screens.booking.BookingScreen
import com.moris.ergo.ui.screens.booking_detail.BookingDetailScreen
import com.moris.ergo.ui.screens.listing_detail.ListingDetailScreen
import com.moris.ergo.ui.screens.my_listings.CreateListingScreen
import com.moris.ergo.ui.screens.my_listings.MyListingsScreen
import com.moris.ergo.ui.screens.ongoing_bookings.OngoingBookingsScreen
import com.moris.ergo.ui.screens.profile.BecomeWorkerScreen
import com.moris.ergo.ui.screens.profile.LoginScreen
import com.moris.ergo.ui.screens.profile.ProfileScreen
import com.moris.ergo.ui.screens.profile.SignupScreen
import com.moris.ergo.ui.screens.search.SearchScreen
import com.moris.ergo.ui.screens.worker_detail.WorkerDetailScreen

@Composable
fun ErgoNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier
    ) {
        composable("home") {
            HomeScreen(
                onSearchClick = { navController.navigate("search") },
                onWorkerClick = { navController.navigate("detail/workers/$it") },
                onListingClick = { navController.navigate("detail/listings/$it") }
            )
        }
        composable("detail/listings/{listing_id}") { backStackEntry ->
            val listingId = backStackEntry.arguments?.getString("listing_id")!!

            ListingDetailScreen(
                listingId = listingId,
                onOwnerClick = { navController.navigate("detail/workers/$it") },
                onBookingClick = { navController.navigate("detail/listings/$listingId/book") }
            )
        }
        composable("search") {
            SearchScreen(
                onListingClick = { navController.navigate("detail/listings/$it") }
            )
        }
        composable("detail/workers/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")!!

            WorkerDetailScreen(
                userId = userId,
                onListingClick = { navController.navigate("detail/listings/$it") },
                onContactClick = { /* open chat / email */ }
            )
        }
        composable("profile") {
            ProfileScreen(
                onLoginClick = { navController.navigate("profile/login") },
                onSignupClick = { navController.navigate("profile/signup") },
                onAddressButtonClick = { navController.navigate("profile/address") },
                onBecomeWorkerClick = { navController.navigate("profile/worker/become") },
                onMyListingsClick = { navController.navigate("profile/worker/listings") }
            )
        }
        composable("profile/login") {
            LoginScreen(
                onSuccess = { navController.popBackStack() }
            )
        }
        composable("profile/signup") {
            SignupScreen(
                onSuccess = { navController.popBackStack() }
            )
        }
        composable("profile/address") {
            AddressListScreen(
                onAddClick = { navController.navigate("profile/address/add") }
            )
        }
        composable("profile/address/add") {
            AddressFormScreen(
                onComplete = { navController.popBackStack() }
            )
        }
        composable("profile/worker/become") {
            BecomeWorkerScreen(
                onSuccess = { navController.popBackStack() }
            )
        }
        composable("profile/worker/become") {
            BecomeWorkerScreen(
                onSuccess = { navController.popBackStack() }
            )
        }
        composable("profile/worker/listings") {
            MyListingsScreen(
                onCreateClick = { navController.navigate("profile/worker/listings/add") },
            )
        }
        composable("profile/worker/listings/add") {
            CreateListingScreen(
                onDone = { navController.popBackStack() }
            )
        }
        composable("detail/listings/{listing_id}/book") { backStackEntry ->
            val listingId = backStackEntry.arguments?.getString("listing_id")!!

            BookingScreen(
                listingId = listingId,
                onSuccess = { navController.popBackStack() }
            )
        }
        composable("ongoing") {
            OngoingBookingsScreen(
                onListingClick = { navController.navigate("detail/listings/$it") },
                onBookingClick = { navController.navigate("detail/booking/$it") }
            )
        }

        composable("detail/booking/{booking_id}") { backStackEntry ->
            val bookingId = backStackEntry.arguments?.getString("booking_id")!!

            BookingDetailScreen(
                bookingId = bookingId,
                onPaymentSuccess = { },
            )

        }

    }
}
