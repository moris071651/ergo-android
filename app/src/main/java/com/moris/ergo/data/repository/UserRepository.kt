package com.moris.ergo.data.repository

import com.moris.ergo.data.dto.CurrentUserResponseDTO
import com.moris.ergo.data.api.ErgoServerApi
import com.moris.ergo.data.dto.UserLoginRequestDTO
import com.moris.ergo.data.dto.UserResponseDTO
import com.moris.ergo.data.dto.UserSignupRequestDTO
import jakarta.inject.Inject
import jakarta.inject.Singleton

interface UserRepository {
    suspend fun getCurrentUser(): CurrentUserResponseDTO
    suspend fun getUserById(userId: String): UserResponseDTO
    suspend fun login(request: UserLoginRequestDTO)
    suspend fun signup(request: UserSignupRequestDTO)
    suspend fun logout()
}

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val api: ErgoServerApi
) : UserRepository {

    override suspend fun getCurrentUser(): CurrentUserResponseDTO {
        return api.getCurrentUser()
    }

    override suspend fun getUserById(userId: String): UserResponseDTO =
        api.getUserById(userId)

    override suspend fun login(request: UserLoginRequestDTO) {
        api.login(request)
    }

    override suspend fun signup(request: UserSignupRequestDTO) {
        api.signup(request)
    }

    override suspend fun logout() {
        api.logout()
    }
}
