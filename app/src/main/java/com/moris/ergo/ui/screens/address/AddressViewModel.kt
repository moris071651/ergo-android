package com.moris.ergo.ui.screens.address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moris.ergo.data.dto.AddressDTO
import com.moris.ergo.data.repository.AddressRepository
import com.moris.ergo.data.dto.CreateAddressRequestDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val repository: AddressRepository
) : ViewModel() {

    private val _addresses = MutableStateFlow<List<AddressDTO>>(emptyList())
    val addresses: StateFlow<List<AddressDTO>> = _addresses

    private val _selectedAddress = MutableStateFlow<AddressDTO?>(null)
    val selectedAddress: StateFlow<AddressDTO?> = _selectedAddress


    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadAddresses() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _addresses.value = repository.getAll()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getAddressById(addressId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val address = repository.getAddressById(addressId)
                _selectedAddress.value = address
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createAddress(address: CreateAddressRequestDTO) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.create(address)
                loadAddresses()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateAddress(id: String, address: CreateAddressRequestDTO) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.update(id, address)
                loadAddresses()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteAddress(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.delete(id)
                loadAddresses()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}
