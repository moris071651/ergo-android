package com.moris.ergo.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moris.ergo.data.dto.AddressDTO
import com.moris.ergo.data.repository.AddressRepository
import com.moris.ergo.data.dto.BecomeWorkerRequestDTO
import com.moris.ergo.data.dto.CurrentUserResponseDTO
import com.moris.ergo.data.dto.CurrentWorkerResponseDTO
import com.moris.ergo.data.dto.UserLoginRequestDTO
import com.moris.ergo.data.dto.UserSignupRequestDTO
import com.moris.ergo.data.repository.UserRepository
import com.moris.ergo.data.repository.WorkerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepo: UserRepository,
    private val workerRepo: WorkerRepository,
    private val addressRepo: AddressRepository
) : ViewModel() {

    private val _user = MutableStateFlow<CurrentUserResponseDTO?>(null)
    val user: StateFlow<CurrentUserResponseDTO?> = _user

    private val _worker = MutableStateFlow<CurrentWorkerResponseDTO?>(null)
    val worker: StateFlow<CurrentWorkerResponseDTO?> = _worker

    private val _addresses = MutableStateFlow<List<AddressDTO>>(emptyList())
    val addresses: StateFlow<List<AddressDTO>> = _addresses

    private val _skillsOptions = MutableStateFlow<List<String>>(emptyList())
    val skillsOptions: StateFlow<List<String>> = _skillsOptions

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadSession() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _user.value = userRepo.getCurrentUser()
                _worker.value = workerRepo.getCurrentWorker()
            } catch (e: Exception) {
                _user.value = null
                _worker.value = null
                _error.value = e.toString()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun login(request: UserLoginRequestDTO, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                userRepo.login(request)
                onSuccess()
            }
            catch (e: Exception) {
                _error.value = e.message ?: "Login failed"
            }
            finally {
                _isLoading.value = false
            }
        }
    }

    fun signup(request: UserSignupRequestDTO, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                userRepo.signup(request)
                onSuccess()
            } catch (e: Exception) {
                _error.value = e.message ?: "Signup failed"
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun loadUserAddresses() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _addresses.value = addressRepo.getAll()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadSkillsOptions() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _skillsOptions.value = workerRepo.getSkillsOptions()
            }
            catch (e: Exception) {
                _error.value = e.message
            }
            finally {
                _isLoading.value = false
            }
        }
    }

    fun becomeWorker(request: BecomeWorkerRequestDTO, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _worker.value = workerRepo.becomeWorker(request)
                onSuccess()
            } catch (e: Exception) {
                _error.value = e.message ?: "Signup failed"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            try { userRepo.logout() } catch (_: Exception) {}
            _user.value = null
            _worker.value = null
            onComplete()
        }
    }

    fun startStripeOnboarding(onUrl: (String) -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val link = workerRepo.getStripeOnboardingLink()
                onUrl(link.url)
            }
            catch (e: Exception) {
                _error.value = e.message
            }
            finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshWorkerStatus() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _worker.value = workerRepo.getCurrentWorker()
            }
            catch (e: Exception) {
                _error.value = e.message ?: "Something Happened"
            }
            finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
