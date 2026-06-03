package com.moris.ergo.ui.screens.edit_profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moris.ergo.data.dto.UserUpdateRequestDTO
import com.moris.ergo.data.dto.WorkerUpdateRequestDTO
import com.moris.ergo.data.repository.UserRepository
import com.moris.ergo.data.repository.WorkerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val workerRepository: WorkerRepository
) : ViewModel() {
    private val _firstName = MutableStateFlow<String>("")
    val firstName: StateFlow<String> = _firstName

    private val _lastName = MutableStateFlow<String>("")
    val lastName: StateFlow<String?> = _lastName

    private val _email = MutableStateFlow<String>("")
    val email: StateFlow<String?> = _email

    private val _bio = MutableStateFlow("")
    val bio: StateFlow<String> = _bio

    private val _serviceRadius = MutableStateFlow(0)
    val serviceRadius: StateFlow<Int> = _serviceRadius

    private val _experienceYears = MutableStateFlow(0)
    val experienceYears: StateFlow<Int> = _experienceYears

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    fun loadUserData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val user = userRepository.getCurrentUser()

                _firstName.value = user.firstName
                _lastName.value = user.lastName
                _email.value = user.email
            }
            catch (e: Exception) {
                _error.value = e.message ?: "Failed to load user data"
            }
            finally {
                _isLoading.value = false
            }
        }
    }

    fun loadWorkerData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val worker = workerRepository.getCurrentWorker()

                _bio.value = worker?.bio!!
                _serviceRadius.value = worker.serviceRadiusKm
                _experienceYears.value = worker.experienceYears
            }
            catch (e: Exception) {
                _error.value = e.message ?: "Failed to load worker data"
            }
            finally {
                _isLoading.value = false
            }
        }
    }

    fun onFirstNameChange(newValue: String) { _firstName.value = newValue }
    fun onLastNameChange(newValue: String) { _lastName.value = newValue }
    fun onEmailChange(newValue: String) { _email.value = newValue }
    fun onBioChange(newValue: String) { _bio.value = newValue }
    fun onRadiusChange(newValue: Int) { _serviceRadius.value = newValue }
    fun onExperienceChange(newValue: Int) { _experienceYears.value = newValue }

    fun uploadImage(fileBytes: ByteArray) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                userRepository.uploadUserPicture(fileBytes)
                _uiEvent.emit(UiEvent.ImageUploadSuccess)
            }
            catch (e: Exception) {
                _error.value = e.message ?: "Failed to upload image"
            }
            finally {
                _isLoading.value = false
            }
        }
    }

    fun updateUserData() {
        if (!isFormValid()) return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val request = UserUpdateRequestDTO(
                    email = _email.value,
                    firstName = _firstName.value,
                    lastName = _lastName.value
                )

                userRepository.updateUser(request)
                _uiEvent.emit(UiEvent.UserUpdateSuccess)
            }
            catch (e: Exception) {
                _error.value = e.message ?: "Failed to update profile"
            }
            finally {
                _isLoading.value = false
            }
        }
    }

    fun updateWorkerData() {
        if (!isFormValid()) return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val request = WorkerUpdateRequestDTO(
                    addressId = null,
                    bio = _bio.value,
                    serviceRadiusKm = _serviceRadius.value,
                    experienceYears = _experienceYears.value,
                    skills = listOf()
                )

                workerRepository.updateWorkerProfile(request)
                _uiEvent.emit(UiEvent.WorkerUpdateSuccess)
            }
            catch (e: Exception) {
                _error.value = e.message ?: "Failed to update worker profile"
            }
            finally {
                _isLoading.value = false
            }
        }
    }

    private fun isFormValid(): Boolean {
        if (_firstName.value.isBlank() || _lastName.value.isBlank()) {
            _error.value = "First and last name cannot be empty"
            return false
        }
        if (!_email.value.contains("@")) {
            _error.value = "Invalid email address"
            return false
        }
        return true
    }

    sealed interface UiEvent {
        object UserUpdateSuccess : UiEvent
        object WorkerUpdateSuccess : UiEvent
        object ImageUploadSuccess : UiEvent
    }
}
