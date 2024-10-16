package com.faroc.flyme.planes.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("plane_model")
data class PlaneModel(
    @Column("name")
    val name: String,
    @Column("seats")
    val seats: Short,
    @Id
    @Column("plane_model_id")
    val id: Long? = null
)
