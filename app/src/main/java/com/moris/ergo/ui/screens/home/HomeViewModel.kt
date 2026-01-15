package com.moris.ergo.ui.screens.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moris.ergo.data.repository.ListingRepository
import com.moris.ergo.data.scheme.Category
import com.moris.ergo.data.scheme.ListingBriefInfo
import com.moris.ergo.data.scheme.WorkerBriefInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel@Inject constructor(
    private val listingRepository: ListingRepository
) : ViewModel() {

    // Loading state
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Error messages
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Username
    private val _username = MutableStateFlow<String?>(null)
    val username: StateFlow<String?> = _username

    // Categories
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    // Featured workers
    private val _featuredWorkers = MutableStateFlow<List<WorkerBriefInfo>>(emptyList())
    val featuredWorkers: StateFlow<List<WorkerBriefInfo>> = _featuredWorkers

    // Popular listings
    private val _popularListings = MutableStateFlow<List<ListingBriefInfo>>(emptyList())
    val popularListings: StateFlow<List<ListingBriefInfo>> = _popularListings

    init {
        loadCategories()
        loadWorkers()
        loadPopularListings()

        _isLoading.value = false
    }

    private  fun loadCategories() {
        _categories.value = listOf(
            Category("", "Plumbing", "\uD83D\uDEE0"),
            Category("", "Electrical", "\uD83C\uDF0C"),
            Category("", "Painting", "\uD83C\uDFA8"),
            Category("", "Carpentry", "\uD83C\uDFAD"),
            Category("", "Cleaning", "\uD83E\uDDF9"),
            Category("", "Delivery", "\uD83D\uDE9A")
        )
    }

    private fun loadWorkers() {
        _featuredWorkers.value = listOf(
            WorkerBriefInfo(
                userId = "user_001",
                pfpUrl = "https://randomuser.me/api/portraits/men/32.jpg",
                name = "John Doe",
                skill = "Electrician",
                rating = 4.8,
                description = "Experienced electrician specializing in residential and commercial wiring solutions. Reliable and punctual. Experienced electrician specializing in residential and commercial wiring solutions. Reliable and punctual.",
                yearsOfExperience = 5
            ),
            WorkerBriefInfo(
                userId = "user_002",
                pfpUrl = "https://randomuser.me/api/portraits/women/44.jpg",
                name = "Sarah Lee",
                skill = "Plumber",
                rating = 4.7,
                description = "Professional plumber with over 8 years of experience. Skilled in fixing leaks, installing pipes, and maintenance work.",
                yearsOfExperience = 8
            ),
            WorkerBriefInfo(
                userId = "user_003",
                pfpUrl = "https://randomuser.me/api/portraits/men/65.jpg",
                name = "Mike Ross",
                skill = "AC & HVAC Specialist",
                rating = 4.9,
                description = "HVAC expert, providing installation, repair, and maintenance services for residential and commercial systems.",
                yearsOfExperience = 6
            ),
            WorkerBriefInfo(
                userId = "user_004",
                pfpUrl = "https://randomuser.me/api/portraits/women/50.jpg",
                name = "Anna Smith",
                skill = "Painter",
                rating = 4.6,
                description = "Interior and exterior painting specialist. Detail-oriented, uses premium paints, and ensures clean finishes.",
                yearsOfExperience = 4
            ),
            WorkerBriefInfo(
                userId = "user_005",
                pfpUrl = "https://randomuser.me/api/portraits/men/12.jpg",
                name = "David Johnson",
                skill = "Carpenter",
                rating = 4.85,
                description = "Skilled carpenter with experience in furniture making, custom cabinets, and home renovations.",
                yearsOfExperience = 7
            )
        )
    }

    private fun loadPopularListings() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _popularListings.value = listingRepository.getPopularListings(limit = 10, city = "sofia")
            }
            catch (e: Exception) {
                Log.e("HomeViewModel", "Failed to load listings", e)
            }
            finally {
                _isLoading.value = false
            }
        }
    }
}
