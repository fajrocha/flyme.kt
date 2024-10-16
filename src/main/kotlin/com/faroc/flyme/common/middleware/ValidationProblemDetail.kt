package com.faroc.flyme.common.middleware

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail

class ValidationProblemDetail : ProblemDetail() {
    private val _errors: MutableMap<String, MutableList<String>> = HashMap()

    val errors: Map<String, List<String>>
        get() = _errors.toMap()

    fun addValidationError(key: String, value: String) {
        _errors.getOrPut(key) { mutableListOf() }.add(value)
    }

    companion object {
        private const val VALIDATION_ERROR_DETAIL = "The provided data is invalid."

        fun create(): ValidationProblemDetail {
            val validationProblemDetail = ValidationProblemDetail()

            validationProblemDetail.setStatus(HttpStatus.BAD_REQUEST)
            validationProblemDetail.detail = VALIDATION_ERROR_DETAIL

            return validationProblemDetail
        }
    }
}