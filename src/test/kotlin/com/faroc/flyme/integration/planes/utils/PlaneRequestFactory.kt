package com.faroc.flyme.integration.planes.utils

import com.faroc.flyme.planes.api.requests.PlaneRequest

class PlaneRequestFactory {
    companion object {
        fun create(planeModel: String = "Airbus 320") : PlaneRequest {
            return PlaneRequest(planeModel)
        }
    }
}