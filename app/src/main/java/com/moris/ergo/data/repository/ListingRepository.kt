package com.moris.ergo.data.repository

import com.moris.ergo.data.api.CreateListingRequestDTO
import com.moris.ergo.data.api.ErgoServerApi
import com.moris.ergo.data.api.ListingResponseDTO
import com.moris.ergo.data.api.ListingResponsePublicDTO
import com.moris.ergo.data.api.ToggleListingActiveDTO
import com.moris.ergo.data.scheme.ListingBriefInfo
import com.moris.ergo.data.scheme.ListingDetailInfo
import jakarta.inject.Inject

interface ListingRepository {
    suspend fun getPopularListings(limit: Int? = null, city: String? = null): List<ListingBriefInfo>
    suspend fun getWorkerListingsById(userId: String): List<ListingBriefInfo>
    suspend fun getListingDetail2(id: String): ListingResponsePublicDTO
    suspend fun getListingById(listingId: String): ListingResponsePublicDTO
    suspend fun getListingDetail(id: String): ListingDetailInfo
    suspend fun getMyListings(): List<ListingResponseDTO>
    suspend fun createListing(request: CreateListingRequestDTO): ListingResponseDTO
    suspend fun deleteListing(id: String)
    suspend fun toggleListingActive(id: String, request: ToggleListingActiveDTO): ListingResponseDTO
}

class ListingRepositoryImpl @Inject constructor(
    private val api: ErgoServerApi
) : ListingRepository {

    override suspend fun getPopularListings(limit: Int?, city: String?): List<ListingBriefInfo> {
        return api.getListings(limit = limit, city = city).map {
            ListingBriefInfo(
                id = it.id,
                title = it.title,
                description = it.description ?: "",
                priceString = it.priceString,
                rating = 4.0, // placeholder
                primaryImageUrl = "" // placeholder for now
            )
        }
    }

    override suspend fun getWorkerListingsById(userId: String): List<ListingBriefInfo> {
        return api.getWorkerListingsById(userId).map {
            ListingBriefInfo(
                id = it.id,
                title = it.title,
                description = it.description ?: "",
                priceString = it.priceString,
                rating = 4.0, // placeholder
                primaryImageUrl = "" // placeholder for now
            )
        }
    }

    override suspend fun getListingDetail(id: String): ListingDetailInfo {
        val resp = api.getListingDetail(id)
        return ListingDetailInfo(
            id = resp.id,
            ownerId = resp.ownerId,
            title = resp.title,
            description = resp.description ?: "",
            priceString = resp.priceString,
            rating = 4.0,
            visitRequired = resp.visitRequired,
            durationDays = resp.durationDays,
            imageUrls = listOf()
        )
    }

    override suspend fun getListingDetail2(id: String): ListingResponsePublicDTO =
        api.getListingDetail(id)

    override suspend fun getListingById(listingId: String): ListingResponsePublicDTO =
        api.getListingById(listingId)

    override suspend fun getMyListings(): List<ListingResponseDTO> = api.getMyListings()
    override suspend fun createListing(request: CreateListingRequestDTO): ListingResponseDTO = api.createListing(request)
    override suspend fun deleteListing(id: String) = api.deleteListing(id)
    override suspend fun toggleListingActive(id: String, request: ToggleListingActiveDTO): ListingResponseDTO =
        api.toggleListingActive(id, request)
}
