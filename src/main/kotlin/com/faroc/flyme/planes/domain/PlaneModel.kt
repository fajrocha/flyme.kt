package com.faroc.flyme.planes.domain

import com.faroc.flyme.planes.api.responses.PlaneModelResponse
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("plane_model")
data class PlaneModel(
    @Column("name")
    val name: String,
    @Column("seats")
    val seats: Short,
    @Column("avg_speed_kmh")
    val avgSpeed: Double,
    @Id
    @Column("plane_model_id")
    val id: Long? = null
)

fun PlaneModel.toResponse() : PlaneModelResponse {
    return PlaneModelResponse(this.id!!, this.name, this.seats, this.avgSpeed)
}
