package com.faroc.flyme.planes.services

import com.faroc.flyme.common.api.errors.Error
import com.faroc.flyme.common.api.errors.NotFoundError
import com.faroc.flyme.common.api.requests.FetchPaginatedRequest
import com.faroc.flyme.common.api.responses.PaginatedResponse
import com.faroc.flyme.planes.api.requests.PlaneRequest
import com.faroc.flyme.planes.api.responses.PlaneResponse
import com.faroc.flyme.planes.domain.Plane
import com.faroc.flyme.planes.domain.errors.PlaneModelNotFound
import com.faroc.flyme.planes.domain.errors.PlaneNotFound
import com.faroc.flyme.planes.domain.toResponse
import com.faroc.flyme.planes.infrastructure.PlaneModelRepository
import com.faroc.flyme.planes.infrastructure.PlaneRepository
import com.faroc.flyme.planes.services.views.toResponse
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service

@Service
class PlaneService(
    private val planeRepository: PlaneRepository,
    private val planeModelRepository: PlaneModelRepository) {

    suspend fun addPlane(planeRequest: PlaneRequest) : Result<PlaneResponse, Error> {
        val planeModel = planeModelRepository.findByName(planeRequest.planeModel)
            ?: return Err(NotFoundError(PlaneModelNotFound.DESCRIPTION, PlaneModelNotFound.CODE))

        val plane = Plane(planeModel.id!!)

        val planeAdded = planeRepository.save(plane)

        return Ok(planeAdded.toResponse(planeModel))
    }

    suspend fun fetchPlane(id: Long) : Result<PlaneResponse, Error> {
        val planeModelView = planeRepository.findByIdWithPlaneModel(id)
            ?: return Err(NotFoundError(PlaneNotFound.DESCRIPTION, PlaneNotFound.CODE))

        return Ok(planeModelView.toResponse())
    }

    suspend fun fetchPlanes(fetchPlanesRequest: FetchPaginatedRequest) : PaginatedResponse<PlaneResponse> {
        val (pageNumber, pageSize) = fetchPlanesRequest
        val totalElements = planeRepository.count()

        val planes = planeRepository
            .findAllWithPlaneModel(pageSize, fetchPlanesRequest.offset)
            .map { planeWithModelView -> planeWithModelView.toResponse() }
            .toList()

        return PaginatedResponse(planes, pageNumber, pageSize, totalElements)
    }
}