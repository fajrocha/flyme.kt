package com.faroc.flyme.flights.infrastructure.repositories

import com.faroc.flyme.flights.domain.Flight
import com.faroc.flyme.flights.services.views.FlightWithDetailsView
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param

interface FlightRepository : CoroutineCrudRepository<Flight, Long> {

    @Query("""
        SELECT
            f.flight_id,
            al.name as airline_name,
            ad.iata_code as departure_iata,
            ad.city as departure_city,
            ad.city as departure_city,
            ad.time_zone as departure_time_zone,
            aa.iata_code as arrival_iata,
            aa.city as arrival_city,
            aa.time_zone as arrival_time_zone,
            pm.name as plane_model_name,
            duration,
            departure_time,
            arrival_time
        FROM flight f
        JOIN airline al USING (airline_id)
        JOIN airport ad ON f.airport_departure_id = ad.airport_id
        JOIN airport aa ON f.airport_arrival_id = aa.airport_id
        JOIN plane p USING (plane_id)
        JOIN plane_model pm USING (plane_model_id)
        WHERE f.flight_id = :id
    """)
    suspend fun findByIdWithFlightDetails(@Param("id") id: Long) : FlightWithDetailsView?

    @Query("""
        SELECT
            f.flight_id,
            al.name as airline_name,
            ad.iata_code as departure_iata,
            ad.city as departure_city,
            ad.city as departure_city,
            ad.time_zone as departure_time_zone,
            aa.iata_code as arrival_iata,
            aa.city as arrival_city,
            aa.time_zone as arrival_time_zone,
            pm.name as plane_model_name,
            duration,
            departure_time,
            arrival_time
        FROM flight f
        JOIN airline al USING (airline_id)
        JOIN airport ad ON f.airport_departure_id = ad.airport_id
        JOIN airport aa ON f.airport_arrival_id = aa.airport_id
        JOIN plane p USING (plane_id)
        JOIN plane_model pm USING (plane_model_id)
        LIMIT :pageSize OFFSET :pageOffset
    """)
    fun findAllWithFlightDetails(@Param("pageSize") pageSize: Int,
                                  @Param("pageOffset") pageOffset: Int) : Flow<FlightWithDetailsView>
}