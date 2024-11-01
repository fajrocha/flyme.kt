package com.faroc.flyme.planes.views

import com.faroc.flyme.planes.api.responses.PlaneModelResponse
import com.faroc.flyme.planes.api.responses.PlaneResponse

data class PlaneWithPlaneModelView(
    val planeId: Long,
    val planeModelId: Long,
    val planeModelName: String,
    val seats: Short,
    val avgSpeed: Double,
)

fun PlaneWithPlaneModelView.toResponse() : PlaneResponse {
    return PlaneResponse(
        this.planeId,
        PlaneModelResponse(this.planeModelId, this.planeModelName, this.seats, this.avgSpeed)
    )
}
