package com.moris.ergo.ui.screens.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moris.ergo.data.dto.ListingResponsePublicDTO
import com.moris.ergo.data.mapper.toListingBriefInfo
import com.moris.ergo.data.repository.ListingRepository
import com.moris.ergo.data.scheme.ListingBriefInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val listingRepository: ListingRepository
) : ViewModel() {

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
            _results.value = search(currentQuery)
            _isLoading.value = false
        }
    }

    private suspend fun search(query: String): List<ListingBriefInfo> {
        var allListings:  List<ListingResponsePublicDTO>? = null;

        try {
            allListings = listingRepository.searchListingByCity(query, "sofia")
        }
        catch (e: Exception) {
            Log.e("SearchViewModel", "Failed to load listings", e)
            allListings = null
        }

        return allListings?.map {
            it.toListingBriefInfo().apply {
                primaryImageUrl = listingRepository.getListingPrimaryImagesById(it.id)?.url ?: ""
            }
        } ?: listOf()
    }
}
