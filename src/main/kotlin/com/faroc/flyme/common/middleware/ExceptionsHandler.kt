package com.faroc.flyme.common.middleware

import org.springframework.validation.method.ParameterErrors
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.HandlerMethodValidationException

@RestControllerAdvice
class ExceptionsHandler {
    @ExceptionHandler(HandlerMethodValidationException::class)
    fun handleValidation(ex: HandlerMethodValidationException): ValidationProblemDetail {
        val validationProblem = ValidationProblemDetail.create()

        (ex.allValidationResults.firstOrNull() as ParameterErrors).fieldErrors.forEach { e ->
            val field = e.field
            val errorMessage = e.defaultMessage

            validationProblem.addValidationError(field, errorMessage!!)
        }

        return validationProblem
    }
}

