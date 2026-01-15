package com.moris.ergo.data.repository

import android.util.Log
import com.moris.ergo.data.api.BecomeWorkerRequestDTO
import com.moris.ergo.data.api.CurrentWorkerResponseDTO
import com.moris.ergo.data.api.ErgoServerApi
import com.moris.ergo.data.api.StripeOnboardingLinkDTO
import com.moris.ergo.data.api.WorkerResponseDTO
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.HttpStatusCode
import jakarta.inject.Inject
import jakarta.inject.Singleton

interface WorkerRepository {
    suspend fun becomeWorker(request: BecomeWorkerRequestDTO): CurrentWorkerResponseDTO
    suspend fun getCurrentWorker(): CurrentWorkerResponseDTO?
    suspend fun getStripeOnboardingLink(): StripeOnboardingLinkDTO
    suspend fun getWorkerById(userId: String): WorkerResponseDTO
}

@Singleton
class WorkerRepositoryImpl @Inject constructor(
    private val api: ErgoServerApi
) : WorkerRepository {
    override suspend fun becomeWorker(request: BecomeWorkerRequestDTO): CurrentWorkerResponseDTO {
        return api.becomeWorker(request)
    }

    override suspend fun getCurrentWorker(): CurrentWorkerResponseDTO? {
        return try {
            api.getCurrentWorker()
        }
        catch (e: ClientRequestException) {
            if (e.response.status == HttpStatusCode.NotFound) {
                null
            }
            else {
                throw e
            }
        }
        catch (e: Exception) {
            Log.e("API", "Network error", e)
            throw e
        }
    }

    override suspend fun getStripeOnboardingLink(): StripeOnboardingLinkDTO =
        api.getStripeOnboardingLink()

    override suspend fun getWorkerById(userId: String): WorkerResponseDTO =
        api.getWorkerById(userId)

}