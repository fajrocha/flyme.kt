package com.faroc.flyme.airline

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
    suspend fun fetchAirline(@PathVariable airlineId: Long) : AirlinesResponse {
        return AirlinesResponse(1, "", "")
    }
}