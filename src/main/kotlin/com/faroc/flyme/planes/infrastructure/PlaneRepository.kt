package com.faroc.flyme.planes.infrastructure

import com.faroc.flyme.planes.domain.Plane
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface PlaneRepository : CoroutineCrudRepository<Plane, Long> {
}