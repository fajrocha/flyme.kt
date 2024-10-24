package com.faroc.flyme.common.api.middleware

import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException

@RestControllerAdvice
class ExceptionsHandler {

    @ExceptionHandler(WebExchangeBindException::class)
    fun handleValidation(ex: WebExchangeBindException): ValidationProblem {
        val validationProblem = ValidationProblem.create()

        ex.bindingResult.fieldErrors.forEach{ fieldError ->
            val fieldName = fieldError.field
            val errorMessage = fieldError.defaultMessage

            validationProblem.addValidationError(fieldName, errorMessage!!)
        }

        return validationProblem
    }
}

