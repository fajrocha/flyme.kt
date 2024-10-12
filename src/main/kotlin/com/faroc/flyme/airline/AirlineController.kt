package com.faroc.flyme.airline

import com.faroc.flyme.airline.requests.AddAirlineRequest
import com.faroc.flyme.airline.responses.AirlinesResponse
import com.faroc.flyme.common.errors.problem
import com.github.michaelbull.result.fold
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("v1/airlines")
class AirlineController(private val service: AirlineService) {

    @PostMapping
    suspend fun addAirlines(@RequestBody request: List<AddAirlineRequest>) : ResponseEntity<List<AirlinesResponse>> {
        return ResponseEntity(service.addAirlines(request), HttpStatus.CREATED)
    }

    @GetMapping
    suspend fun fetchAirlines() : List<AirlinesResponse> {
        return service.fetchAirlines()
    }

    @GetMapping("{airlineId}")
    suspend fun fetchAirline(@PathVariable airlineId: Long) : ResponseEntity<*> {
        val result = service.fetchAirline(airlineId)

        return result.fold(
            success = { ResponseEntity.ok(result.value) },
            failure = { result.error.problem() }
        )
    }
}

