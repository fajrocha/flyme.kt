package com.faroc.flyme.planes.services

import com.faroc.flyme.common.api.errors.Error
import com.faroc.flyme.common.api.errors.NotFoundError
import com.faroc.flyme.planes.api.requests.PlaneModelRequest
import com.faroc.flyme.planes.api.responses.PlaneModelResponse
import com.faroc.flyme.planes.domain.PlaneModel
import com.faroc.flyme.planes.domain.errors.PlaneModelNotFound
import com.faroc.flyme.planes.infrastructure.PlaneModelRepository
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service

@Service
class PlaneModelService(private val planeModelRepository: PlaneModelRepository) {

    suspend fun addPlaneModels(planeModels: List<PlaneModelRequest>) : List<PlaneModelResponse> {
        val planesToAdd = planeModels.map {
            (name, seats) -> PlaneModel(name, seats)
        }

        val planesAdded = planeModelRepository.saveAll(planesToAdd).toList()

        return planesAdded.map { (name, seats, id) -> PlaneModelResponse(id!!, name, seats) }
    }

    suspend fun fetchPlaneModels() : List<PlaneModelResponse> {
        val planesAdded = planeModelRepository.findAll().toList()

        return planesAdded.map { (name, seats, id) -> PlaneModelResponse(id!!, name, seats) }
    }

    suspend fun fetchPlaneModelById(id: Long) : Result<PlaneModelResponse, Error> {
        val planeFetched = planeModelRepository.findById(id)
            ?: return Err(NotFoundError(PlaneModelNotFound.DESCRIPTION, PlaneModelNotFound.CODE))

        return Ok(PlaneModelResponse(planeFetched.id!!, planeFetched.name, planeFetched.seats))
    }
}