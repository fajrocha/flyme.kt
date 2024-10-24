package com.faroc.flyme.planes.domain.errors

class PlaneNotFound {
    companion object {
        const val DESCRIPTION = "Plane requested was not found."
        const val CODE = "Plane.NotFound"
    }
}