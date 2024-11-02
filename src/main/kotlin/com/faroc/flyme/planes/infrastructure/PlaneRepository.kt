package com.faroc.flyme.planes.infrastructure

import com.faroc.flyme.planes.domain.Plane
import com.faroc.flyme.planes.services.views.FlightPlaneView
import com.faroc.flyme.planes.services.views.PlaneWithPlaneModelView
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param

interface PlaneRepository : CoroutineCrudRepository<Plane, Long> {
    @Query("""
        SELECT p.plane_id, pm.plane_model_id, pm.name AS plane_model_name, pm.seats, pm.avg_speed_kmh as avg_speed
        FROM plane p
        JOIN plane_model pm 
        USING (plane_model_id)
        WHERE p.plane_id = :id
    """)
    suspend fun findByIdWithPlaneModel(@Param("id") id: Long): PlaneWithPlaneModelView?

    @Query("""
        SELECT p.plane_id, pm.avg_speed_kmh as avg_speed
        FROM plane p
        JOIN plane_model pm 
        USING (plane_model_id)
        WHERE p.plane_id = :id
    """)
    suspend fun findByIdFlightPlane(@Param("id") id: Long): FlightPlaneView?

    @Query("""
        SELECT p.plane_id, pm.plane_model_id, pm.name AS plane_model_name, pm.seats, pm.avg_speed_kmh as avg_speed
        FROM plane p
        JOIN plane_model pm
        USING (plane_model_id)
        LIMIT :pageSize OFFSET :pageOffset
    """)
    fun findAllWithPlaneModel(
        @Param("pageSize") pageSize: Int,
        @Param("pageOffset") pageOffset: Int): Flow<PlaneWithPlaneModelView>
}