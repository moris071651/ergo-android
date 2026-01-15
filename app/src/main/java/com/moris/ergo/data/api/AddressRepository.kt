package com.moris.ergo.data.api

import jakarta.inject.Inject
import jakarta.inject.Singleton

interface AddressRepository {
    suspend fun getAll(): List<AddressDTO>
    suspend fun create(address: CreateAddressRequestDTO): AddressDTO
    suspend fun update(id: String, address: CreateAddressRequestDTO): AddressDTO
    suspend fun getAddressById(addressId: String): AddressDTO
    suspend fun delete(id: String)
}

@Singleton
class AddressRepositoryImpl @Inject constructor(
    private val api: ErgoServerApi
) : AddressRepository {
    override suspend fun getAll(): List<AddressDTO> =
        api.getAllAddressesCurrentUser()

    override suspend fun create(address: CreateAddressRequestDTO): AddressDTO =
        api.createAddress(address)

    override suspend fun getAddressById(addressId: String): AddressDTO =
        api.getAddressById(addressId)

    override suspend fun update(id: String, address: CreateAddressRequestDTO): AddressDTO =
        api.updateAddress(id, address)

    override suspend fun delete(id: String) = api.deleteAddress(id)
}
