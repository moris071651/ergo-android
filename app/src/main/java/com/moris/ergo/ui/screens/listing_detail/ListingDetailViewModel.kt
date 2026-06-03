package com.moris.ergo.ui.screens.listing_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moris.ergo.data.repository.ListingRepository
import com.moris.ergo.data.repository.UserRepository
import com.moris.ergo.data.repository.WorkerRepository
import com.moris.ergo.data.scheme.ListingDetailInfo
import com.moris.ergo.data.scheme.WorkerSummaryInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ListingDetailUiState(
    val isLoading: Boolean = true,
    val listing: ListingDetailInfo? = null,
    val worker: WorkerSummaryInfo? = null,
    val customerUnauthenticated: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ListingDetailViewModel @Inject constructor(
    private val listingRepository: ListingRepository,
    private val workerRepository: WorkerRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ListingDetailUiState())
    val uiState: StateFlow<ListingDetailUiState> = _uiState.asStateFlow()

    fun loadListing(listingId: String) {
        viewModelScope.launch {
            _uiState.value = ListingDetailUiState(isLoading = true)

            val l = listingRepository.getListingById(listingId)
            val w = workerRepository.getWorkerById(l.ownerId)
            val u = userRepository.getUserById(w.userId)

            val listing = ListingDetailInfo(
                id = l.id,
                title = l.title,
                description = l.description ?: "",
                priceString = l.priceString,
                rating = 4.8,
                visitRequired = l.visitRequired,
                durationDays = l.durationDays,
                ownerId = l.ownerId,
                imageUrls = listingRepository.getListingImagesById(listingId).map { it.url }
            )

            val worker = WorkerSummaryInfo(
                id = w.userId,
                name = "${u.firstName} ${u.lastName}",
                skill = w.skills.firstOrNull() ?: "",
                pfpUrl = u.profileImageUrl ?: "",
                rating = 4.9,
                yearsOfExperience = w.experienceYears
            )

            val customerUnauthenticated = try {
                val cu = userRepository.getCurrentUser()
                cu.id != u.id
            }
            catch (_: Exception) {
                false
            }

            _uiState.value = ListingDetailUiState(
                isLoading = false,
                listing = listing,
                customerUnauthenticated = customerUnauthenticated,
                worker = worker
            )
        }
    }
}
