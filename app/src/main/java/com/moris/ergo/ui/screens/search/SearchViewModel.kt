package com.moris.ergo.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moris.ergo.data.scheme.ListingBriefInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SearchViewModel @Inject constructor() : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _results = MutableStateFlow<List<ListingBriefInfo>>(emptyList())
    val results: StateFlow<List<ListingBriefInfo>> = _results

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
    }

    fun onSearchClick() {
        val currentQuery = query.value
        if (currentQuery.isBlank()) return

        viewModelScope.launch {
            _isLoading.value = true

            delay(600)

            _results.value = fakeSearch(currentQuery)
            _isLoading.value = false
        }
    }

    private fun fakeSearch(query: String): List<ListingBriefInfo> {
        val allListings = listOf(
            ListingBriefInfo(
                id = "1",
                title = "Home Electrical Repair",
                priceString = "$80 / hr",
                rating = 4.8,
                description = "s",
                primaryImageUrl = ""
            ),
            ListingBriefInfo(
                id = "2",
                title = "Plumbing Services",
                priceString = "$60 / hr",
                rating = 4.6,
                description = "s",
                primaryImageUrl = ""
            ),
            ListingBriefInfo(
                id = "4",
                title = "Home Electrical Repair Service",
                description = "Professional electrical repair service for homes. Includes wiring fixes, socket replacement, and safety inspection. Fast, reliable, and guaranteed work for your safety and comfort.",
                priceString = "$80",
                rating = 4.8,
                primaryImageUrl = "https://img.freepik.com/free-photo/lavender-field-sunset-near-valensole_268835-3910.jpg?semt=ais_hybrid&w=740&q=80"
            ),
            ListingBriefInfo(
                id = "3",
                title = "House Painting",
                priceString = "$100 / day",
                rating = 4.7,
                description = "s",
                primaryImageUrl = ""
            )
        )

        return allListings.filter {
            it.title.contains(query, ignoreCase = true)
        }
    }
}
