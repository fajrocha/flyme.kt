package com.faroc.flyme.integration.planes.utils

import com.faroc.flyme.planes.api.requests.PlaneRequest

class PlaneTestsFactory {
    companion object {
        fun createAddRequest(planeModel: String = "Airbus 320") : PlaneRequest {
            return PlaneRequest(planeModel)
        }
    }
}