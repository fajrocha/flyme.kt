package com.faroc.flyme.planes.domain

import com.faroc.flyme.planes.api.responses.PlaneResponse
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("plane")
data class Plane(
    @Column("plane_model_id")
    val planeModel: Long,
    @Id
    @Column("plane_id")
    val id: Long? = null
)

fun Plane.toResponse(planeModel: PlaneModel) : PlaneResponse {
    return PlaneResponse(this.id!!, planeModel.toResponse())
}