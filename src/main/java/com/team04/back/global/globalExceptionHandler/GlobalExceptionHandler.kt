package com.team04.back.global.globalExceptionHandler

import com.team04.back.global.exception.ServiceException
import com.team04.back.global.rsData.RsData
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.reactive.function.client.WebClientResponseException

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(NoSuchElementException::class)
    fun handle(ex: NoSuchElementException): ResponseEntity<RsData<Void>> {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(RsData("404-1", "해당 데이터가 존재하지 않습니다."))
    }

    @ExceptionHandler(ServiceException::class)
    fun handle(ex: ServiceException): ResponseEntity<RsData<Void>> {
        val rsData: RsData<Void> = ex.rsData

        return ResponseEntity
            .status(rsData.statusCode)
            .body(rsData)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handle(ex: IllegalArgumentException): ResponseEntity<RsData<Void>> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(RsData("400-1", ex.message ?: ""))
    }

    @ExceptionHandler(WebClientResponseException.Unauthorized::class)
    fun handleUnauthorized(ex: WebClientResponseException): ResponseEntity<*> {
        val body = mapOf(
            "error" to "외부 API 인증 오류",
            "message" to ex.message
        )

        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(body)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handle(ex: MethodArgumentNotValidException): ResponseEntity<RsData<Void>> {
        val message = ex.bindingResult.allErrors
            .filter { it is FieldError }
            .map { it as FieldError }
            .map { "${it.field}-${it.code}-${it.defaultMessage}" }
            .sorted()
            .joinToString("\n")

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(RsData("400-1", message))
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handle(ex: HttpMessageNotReadableException): ResponseEntity<RsData<Void>> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(RsData("400-1", "요청 본문이 올바르지 않습니다."))
    }
}