package com.moris.ergo.data.mapper

import com.moris.ergo.data.dto.PopularWorkerResponseDTO
import com.moris.ergo.data.scheme.WorkerBriefInfo

fun PopularWorkerResponseDTO.toWorkerBriefInfo(): WorkerBriefInfo {
    return WorkerBriefInfo(
        userId = this.userId,
        pfpUrl = "",
        name = this.user.firstName + " " + this.user.lastName,
        skill = this.skills.getOrNull(0) ?: "",
        rating = 4.8,
        description = this.bio ?: "",
        yearsOfExperience = this.experienceYears ?: 0
    )
}
