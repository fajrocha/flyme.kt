package com.faroc.flyme.common.api.errors

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity

sealed class Error(val errorType: ErrorTypes, val description: String? = null, val code: String? = null) {
}

class NotFoundError(description: String? = null, code: String? = "NotFound") : Error(
    errorType = ErrorTypes.NOT_FOUND,
    description = description,
    code = code
)

class UnexpectedError(description: String? = null, code: String? = "UnexpectedError") : Error(
    errorType = ErrorTypes.UNEXPECTED,
    description = description,
    code = code
)

class ConflictError(description: String? = null, code: String? = "Conflict") : Error(
    errorType = ErrorTypes.CONFLICT,
    description = description,
    code = code
)

fun Error.toProblem(): ResponseEntity<ProblemDetail> {
    val statusCode = when (this.errorType) {
        ErrorTypes.NOT_FOUND -> HttpStatus.NOT_FOUND
        ErrorTypes.CONFLICT -> HttpStatus.CONFLICT
        ErrorTypes.UNEXPECTED -> HttpStatus.INTERNAL_SERVER_ERROR
    }
    val problem = ProblemDetail.forStatusAndDetail(statusCode, this.description)
    problem.title = this.code

    return ResponseEntity(problem, statusCode)
}

enum class ErrorTypes {
    CONFLICT,
    NOT_FOUND,
    UNEXPECTED,
}