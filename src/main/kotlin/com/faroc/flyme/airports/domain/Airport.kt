package com.faroc.flyme.airports.domain

import com.faroc.flyme.airports.api.response.AirportResponse
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("airport")
class Airport private constructor(
    @Column("iata_code")
    val iataCode: String,
    @Column("name")
    val name: String,
    @Column("city")
    val city: String,
    @Column("country")
    val country: String,
    @Id
    @Column("airport_id")
    val id: Long? = null,
) {
    companion object {
        fun create(iataCode: String, name: String, city: String, country: String, id: Long? = null) : Airport {
            return Airport(iataCode.uppercase(), name, city, country, id)
        }
    }
}

fun Airport.toResponse() : AirportResponse {
    return AirportResponse(this.id!!, this.iataCode, this.name, this.city, this.country)
}
