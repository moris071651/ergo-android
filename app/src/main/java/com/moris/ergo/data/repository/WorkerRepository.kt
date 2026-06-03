package com.moris.ergo.data.repository

import android.util.Log
import com.moris.ergo.data.dto.BecomeWorkerRequestDTO
import com.moris.ergo.data.dto.CurrentWorkerResponseDTO
import com.moris.ergo.data.api.ErgoServerApi
import com.moris.ergo.data.dto.PopularWorkerResponseDTO
import com.moris.ergo.data.dto.StripeOnboardingLinkDTO
import com.moris.ergo.data.dto.WorkerResponseDTO
import com.moris.ergo.data.dto.WorkerUpdateRequestDTO
import com.moris.ergo.data.mapper.toWorkerBriefInfo
import com.moris.ergo.data.scheme.WorkerBriefInfo
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.HttpStatusCode
import jakarta.inject.Inject
import jakarta.inject.Singleton

interface WorkerRepository {
    suspend fun becomeWorker(request: BecomeWorkerRequestDTO): CurrentWorkerResponseDTO
    suspend fun getCurrentWorker(): CurrentWorkerResponseDTO?
    suspend fun getStripeOnboardingLink(): StripeOnboardingLinkDTO
    suspend fun getWorkerById(userId: String): WorkerResponseDTO
    suspend fun getPopularWorkerByCity(limit: Int, city: String): List<WorkerBriefInfo>
    suspend fun updateWorkerProfile(request: WorkerUpdateRequestDTO): WorkerResponseDTO
    suspend fun getSkillsOptions(): List<String>
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

    override suspend fun getPopularWorkerByCity(limit: Int, city: String): List<WorkerBriefInfo> =
        api.getPopularWorker(limit = limit, city = city).map {
            it.toWorkerBriefInfo()
        }

    override suspend fun updateWorkerProfile(request: WorkerUpdateRequestDTO): WorkerResponseDTO =
        api.updateWorkerProfile(request)

    override suspend fun getSkillsOptions(): List<String> =
        api.getSkillsOptions()

}
