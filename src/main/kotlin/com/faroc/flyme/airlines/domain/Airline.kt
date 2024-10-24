package com.faroc.flyme.airlines.domain

import com.faroc.flyme.airlines.api.responses.AirlinesResponse
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

fun Airline.toResponse() : AirlinesResponse {
    return AirlinesResponse(this.id!!, this.name, this.country)
}