package com.faroc.flyme.common.api.serializes

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import java.math.BigDecimal
import java.math.RoundingMode

class TwoDecimalDoubleSerializer : JsonSerializer<Double>() {
    override fun serialize(value: Double, gen: JsonGenerator, serializers: SerializerProvider) {
        val rounded = BigDecimal(value).setScale(2, RoundingMode.HALF_UP)
        gen.writeNumber(rounded)
    }
}