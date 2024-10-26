package com.faroc.flyme.planes.services

import com.faroc.flyme.common.api.errors.Error
import com.faroc.flyme.common.api.errors.NotFoundError
import com.faroc.flyme.planes.api.requests.PlaneModelRequest
import com.faroc.flyme.planes.api.requests.toDomain
import com.faroc.flyme.planes.api.responses.PlaneModelResponse
import com.faroc.flyme.planes.domain.errors.PlaneModelNotFound
import com.faroc.flyme.planes.domain.toResponse
import com.faroc.flyme.planes.infrastructure.PlaneModelRepository
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service

@Service
class PlaneModelService(private val planeModelRepository: PlaneModelRepository) {
    suspend fun addPlaneModel(planeModelRequest: PlaneModelRequest) : PlaneModelResponse {
        val planeAdded = planeModelRepository.save(planeModelRequest.toDomain())

        return planeAdded.toResponse()
    }

    suspend fun fetchPlaneModels() : List<PlaneModelResponse> {
        return planeModelRepository
            .findAll()
            .map { planeModel -> planeModel.toResponse() }
            .toList()
    }

    suspend fun fetchPlaneModelById(id: Long) : Result<PlaneModelResponse, Error> {
        val planeFetched = planeModelRepository.findById(id)
            ?: return Err(NotFoundError(PlaneModelNotFound.DESCRIPTION, PlaneModelNotFound.CODE))

        return Ok(planeFetched.toResponse())
    }
}