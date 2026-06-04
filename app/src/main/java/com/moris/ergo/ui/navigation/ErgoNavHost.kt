package com.moris.ergo.ui.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import com.moris.ergo.ui.screens.edit_profile.EditUserScreen
import com.moris.ergo.ui.screens.edit_profile.EditWorkerScreen
import com.moris.ergo.ui.screens.listing_detail.ListingDetailScreen
import com.moris.ergo.ui.screens.my_listings.CreateListingScreen
import com.moris.ergo.ui.screens.my_listings.EditListingScreen
import com.moris.ergo.ui.screens.my_listings.MyListingsScreen
import com.moris.ergo.ui.screens.ongoing_bookings.OngoingBookingsScreen
import com.moris.ergo.ui.screens.profile.BecomeWorkerScreen
import com.moris.ergo.ui.screens.profile.LoginScreen
import com.moris.ergo.ui.screens.profile.ProfileScreen
import com.moris.ergo.ui.screens.profile.SignupScreen
import com.moris.ergo.ui.screens.search.SearchScreen
import com.moris.ergo.ui.screens.worker_detail.WorkerDetailScreen
import androidx.core.net.toUri

@Composable
fun ErgoNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val onContactClick = { email: String, context: Context ->
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = ("mailto:" + Uri.encode(email)).toUri()

            setPackage("com.google.android.apps.gmail")
        }

        try {
            context.startActivity(intent)
        }
        catch (_: Exception) {
            val fallbackIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = ("mailto:" + Uri.encode(email)).toUri()
            }

            context.startActivity(Intent.createChooser(fallbackIntent, "Send email via..."))
        }
    }

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
                onContactClick = onContactClick
            )
        }
        composable("profile") {
            ProfileScreen(
                onLoginClick = { navController.navigate("profile/login") },
                onSignupClick = { navController.navigate("profile/signup") },
                onMyPersonalInfoClick = { navController.navigate("profile/update/user") },
                onMyWorkerInfoClick = { navController.navigate("profile/update/worker") },
                onMyAddressesClick = { navController.navigate("profile/address") },
                onMyListingsClick = { navController.navigate("profile/worker/listings") },
                onBecomeWorkerClick = { navController.navigate("profile/worker/become") }
            )
        }
        composable("profile/update/user") {
            EditUserScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("profile/update/worker") {
            EditWorkerScreen(
                onNavigateBack = { navController.popBackStack() }
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
                onEditClick = { navController.navigate("profile/address/edit/$it") }
            )
        }
        composable("profile/address/add") {
            AddressFormScreen(
                onComplete = { navController.popBackStack() }
            )
        }
        composable("profile/address/edit/{addressId}") { backStackEntry ->
            val addressId = backStackEntry.arguments?.getString("addressId")!!

            AddressFormScreen(
                addressId = addressId,
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
                onCardClick = { navController.navigate("detail/listings/$it") },
                onEditClick = { navController.navigate("profile/worker/listings/edit/$it") }
            )
        }
        composable("profile/worker/listings/add") {
            CreateListingScreen(
                onDone = { navController.popBackStack() }
            )
        }
        composable("profile/worker/listings/edit/{listing_id}") { backStackEntry ->
            val listingId = backStackEntry.arguments?.getString("listing_id")!!

            EditListingScreen(
                listingId = listingId,
                onSuccess = { navController.popBackStack() },
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
                onContactClick = onContactClick,
                onPaymentSuccess = { },
            )
        }
    }
}
