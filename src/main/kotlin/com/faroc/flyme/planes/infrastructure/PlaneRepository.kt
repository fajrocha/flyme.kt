package com.faroc.flyme.planes.infrastructure

import com.faroc.flyme.planes.domain.Plane
import com.faroc.flyme.planes.views.PlaneWithModelView
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param

interface PlaneRepository : CoroutineCrudRepository<Plane, Long> {
    @Query("""
        SELECT p.plane_id, pm.plane_model_id, pm.name AS plane_model_name, pm.seats
        FROM plane p
        JOIN plane_model pm 
        USING (plane_model_id)
        WHERE p.plane_id = :id
    """)
    fun findByIdWithPlaneModel(@Param("id") id: Long): Flow<PlaneWithModelView?>

    @Query("""
        SELECT p.plane_id, pm.plane_model_id, pm.name AS plane_model_name, pm.seats
        FROM plane p
        JOIN plane_model pm
        USING (plane_model_id)
        LIMIT :pageSize OFFSET :pageOffset
    """)
    fun findAllWithPlaneModel(
        @Param("pageSize") pageSize: Int,
        @Param("pageOffset") pageOffset: Int): Flow<PlaneWithModelView>
}