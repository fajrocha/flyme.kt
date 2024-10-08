package com.faroc.flyme.planes.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("plane_model")
data class PlaneModel(
    @Id
    val id: Long? = null,
    val name: String,
    val seats: Short
)
