package com.faroc.flyme.airlines.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("airline")
data class Airline(
    @Column("name")
    val name: String,
    @Column("country")
    val country: String,
    @Id
    @Column("airline_id")
    val id: Long? = null,
)

