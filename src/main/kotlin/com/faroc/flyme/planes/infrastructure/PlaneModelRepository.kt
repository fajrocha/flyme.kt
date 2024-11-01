package com.faroc.flyme.planes.infrastructure

import com.faroc.flyme.planes.domain.PlaneModel
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface PlaneModelRepository : CoroutineCrudRepository<PlaneModel, Long> {
    suspend fun findByName(name: String): PlaneModel?
}