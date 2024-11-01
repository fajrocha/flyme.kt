package com.faroc.flyme.flights.services

import com.faroc.flyme.airlines.domain.errors.AirlineNotFound
import com.faroc.flyme.airlines.infrastructure.AirlineRepository
import com.faroc.flyme.airports.infrastructure.AirportRepository
import com.faroc.flyme.common.api.errors.Error
import com.faroc.flyme.common.api.errors.NotFoundError
import com.faroc.flyme.flights.api.requests.ScheduleFlightRequest
import com.faroc.flyme.flights.api.responses.ScheduleFlightResponse
import com.faroc.flyme.flights.domain.Flight
import com.faroc.flyme.flights.domain.errors.FlightDepartAirportNotFound
import com.faroc.flyme.flights.domain.toResponse
import com.faroc.flyme.flights.infrastructure.airportgap.responses.toFlightAirportsDetails
import com.faroc.flyme.flights.infrastructure.repositories.FlightRepository
import com.faroc.flyme.flights.services.abstractions.DistanceService
import com.faroc.flyme.planes.domain.errors.PlaneNotFound
import com.faroc.flyme.planes.infrastructure.PlaneRepository
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.springframework.stereotype.Service

@Service
class FlightService(
    private val airportRepository: AirportRepository,
    private val planeRepository: PlaneRepository,
    private val airlineRepository: AirlineRepository,
    private val distanceService: DistanceService,
    private val flightRepository: FlightRepository,
) {
    suspend fun scheduleFlight(scheduleFlightRequest: ScheduleFlightRequest) : Result<ScheduleFlightResponse, Error> {
        val (
            departureIataCode,
            arrivalIataCode,
            planeModelName,
            airlineName,
            departureTime,
        ) = scheduleFlightRequest

        val departureAirport = airportRepository.findByIataCode(departureIataCode)
            ?: return Err(NotFoundError(FlightDepartAirportNotFound.DESCRIPTION, FlightDepartAirportNotFound.CODE))

        val arrivalAirport = airportRepository.findByIataCode(arrivalIataCode)
            ?: return Err(NotFoundError(FlightDepartAirportNotFound.DESCRIPTION, FlightDepartAirportNotFound.CODE))

        val flightPlane = planeRepository.findByIdFlightPlane(planeModelName)
            ?: return Err(NotFoundError(PlaneNotFound.DESCRIPTION, PlaneNotFound.CODE))

        val airline = airlineRepository.findByName(airlineName)
            ?: return Err(NotFoundError(AirlineNotFound.DESCRIPTION, AirlineNotFound.CODE))

        val airportsReport = distanceService.fetchDistanceBetweenAirports(departureIataCode, arrivalIataCode)

        val flight = Flight.create(
            departureAirport.id!!,
            arrivalAirport.id!!,
            flightPlane,
            airline.id!!,
            departureTime,
            airportsReport.data.attributes.toFlightAirportsDetails()
        )

        flightRepository.save(flight)

        return Ok(flight.toResponse())
    }
}