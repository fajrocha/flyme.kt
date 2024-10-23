package com.faroc.flyme.planes.infrastructure

import com.faroc.flyme.planes.domain.PlaneModel
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface PlaneModelRepository : CoroutineCrudRepository<PlaneModel, Long> {
    fun findByName(name: String): Flow<PlaneModel?>
}