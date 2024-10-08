package com.faroc.flyme.airline

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("airline")
data class Airline(
    @Id
    @Column("airline_id")
    val id: Long? = null,
    @Column("name")
    val name: String,
    @Column("country")
    val country: String
)