package com.faroc.flyme.common.api.middleware

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail

class ValidationProblem private constructor() : ProblemDetail() {
    val errors: MutableMap<String, MutableList<String>> = mutableMapOf()

    fun addValidationError(key: String, value: String) {
        errors.getOrPut(key) { mutableListOf() }.add(value)
    }

    companion object {
        const val DETAIL = "The provided data is invalid."

        fun create(): ValidationProblem {
            val validationProblemDetail = ValidationProblem()

            validationProblemDetail.setStatus(HttpStatus.BAD_REQUEST)
            validationProblemDetail.detail = DETAIL

            return validationProblemDetail
        }
    }
}