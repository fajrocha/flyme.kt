package com.faroc.flyme.flights.services

import com.faroc.flyme.airlines.domain.errors.AirlineNotFound
import com.faroc.flyme.airlines.infrastructure.AirlineRepository
import com.faroc.flyme.airports.infrastructure.AirportRepository
import com.faroc.flyme.common.api.errors.Error
import com.faroc.flyme.common.api.errors.NotFoundError
import com.faroc.flyme.flights.api.requests.ScheduleFlightRequest
import com.faroc.flyme.flights.api.responses.ScheduleFlightResponse
import com.faroc.flyme.flights.domain.Flight
import com.faroc.flyme.flights.domain.FlightAirportDetails
import com.faroc.flyme.flights.domain.FlightAirportsDetails
import com.faroc.flyme.flights.domain.errors.FlightDepartAirportNotFound
import com.faroc.flyme.flights.domain.toResponse
import com.faroc.flyme.flights.infrastructure.repositories.FlightRepository
import com.faroc.flyme.flights.services.abstractions.AirportDataService
import com.faroc.flyme.planes.domain.errors.PlaneNotFound
import com.faroc.flyme.planes.infrastructure.PlaneRepository
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FlightsSchedulerService(
    private val airportRepository: AirportRepository,
    private val planeRepository: PlaneRepository,
    private val airlineRepository: AirlineRepository,
    private val airportDataService: AirportDataService,
    private val flightRepository: FlightRepository,
) {
    @Transactional
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

        val airportsDistanceData = airportDataService.fetchDistanceBetweenAirports(
            departureIataCode,
            arrivalIataCode
        )

        val flightDetails = FlightAirportsDetails(
            airportsDistanceData.data.attributes.kilometers,
            FlightAirportDetails(departureAirport.timeZone),
            FlightAirportDetails(arrivalAirport.timeZone)
        )

        val flightToSchedule = Flight.create(
            departureAirport.id!!,
            arrivalAirport.id!!,
            flightPlane,
            airline.id!!,
            departureTime,
            flightDetails
        )

        val flightScheduled = flightRepository.save(flightToSchedule)

        return Ok(flightScheduled.toResponse())
    }
}