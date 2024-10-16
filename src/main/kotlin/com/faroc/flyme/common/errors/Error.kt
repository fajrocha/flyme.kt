package com.faroc.flyme.common.errors

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity

sealed class Error(val errorType: ErrorTypes, val description: String? = null, val code: String? = null) {
}

fun Error.problem(): ResponseEntity<ProblemDetail> {
    val statusCode = when (this.errorType) {
        ErrorTypes.NOT_FOUND -> HttpStatus.NOT_FOUND
        ErrorTypes.CONFLICT -> HttpStatus.CONFLICT
    }
    val problem = ProblemDetail.forStatusAndDetail(statusCode, this.description)

    return ResponseEntity(problem, statusCode)
}

class NotFoundError(description: String? = null, code: String? = null) : Error(
    errorType = ErrorTypes.NOT_FOUND,
    description = description,
    code = code
)

class ConflictError(description: String? = null, code: String? = null) : Error(
    errorType = ErrorTypes.CONFLICT,
    description = description,
    code = code
)

enum class ErrorTypes {
    CONFLICT,
    NOT_FOUND,
}