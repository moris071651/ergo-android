package com.moris.ergo.ui.screens.worker_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moris.ergo.data.dto.UserResponseDTO
import com.moris.ergo.data.dto.WorkerResponseDTO
import com.moris.ergo.data.repository.ListingRepository
import com.moris.ergo.data.repository.UserRepository
import com.moris.ergo.data.repository.WorkerRepository
import com.moris.ergo.data.scheme.ListingBriefInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class WorkerDetailViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val workerRepository: WorkerRepository,
    private val listingRepository: ListingRepository
) : ViewModel() {

    private val _user = MutableStateFlow<UserResponseDTO?>(null)
    val user: StateFlow<UserResponseDTO?> = _user

    private val _worker = MutableStateFlow<WorkerResponseDTO?>(null)
    val worker: StateFlow<WorkerResponseDTO?> = _worker

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _listings = MutableStateFlow<List<ListingBriefInfo>>(emptyList())
    val listings: StateFlow<List<ListingBriefInfo>> = _listings

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadWorker(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _user.value = userRepository.getUserById(userId)
                _worker.value = workerRepository.getWorkerById(userId)
                _listings.value = listingRepository.getWorkerListingsById(userId).map {
                    it.apply {
                        primaryImageUrl = listingRepository.getListingPrimaryImagesById(it.id)?.url ?: ""
                    }
                }
            }
            catch (e: Exception) {
                _error.value = e.message ?: "Something Happened"
            }
            finally {
                _isLoading.value = false
            }
        }
    }
}
