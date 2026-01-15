package com.moris.ergo.data.repository

import android.util.Log
import com.moris.ergo.data.api.CurrentUserResponseDTO
import com.moris.ergo.data.api.ErgoServerApi
import com.moris.ergo.data.api.UserLoginRequestDTO
import com.moris.ergo.data.api.UserResponseDTO
import com.moris.ergo.data.api.UserSignupRequestDTO
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
