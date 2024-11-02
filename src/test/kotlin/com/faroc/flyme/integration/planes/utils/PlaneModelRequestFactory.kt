package com.faroc.flyme.integration.planes.utils

import com.faroc.flyme.planes.api.requests.PlaneModelRequest

class PlaneModelRequestFactory {
    companion object {
        fun create(name: String = "Airbus 320", seats: Short = 70, avgSpeed: Double = 777.84) : PlaneModelRequest {
            return PlaneModelRequest(name, seats, avgSpeed)
        }
    }
}