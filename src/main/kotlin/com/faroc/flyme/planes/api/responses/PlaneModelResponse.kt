package com.faroc.flyme.planes.api.responses

import com.faroc.flyme.common.api.serializes.TwoDecimalDoubleSerializer
import com.fasterxml.jackson.databind.annotation.JsonSerialize

data class PlaneModelResponse(
    val id: Long,
    val name: String,
    val seats: Short,
    @field:JsonSerialize(using = TwoDecimalDoubleSerializer::class)
    val avgSpeed: Double,)