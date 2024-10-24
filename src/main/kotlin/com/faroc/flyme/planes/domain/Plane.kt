package com.faroc.flyme.planes.domain

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