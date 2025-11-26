package com.acme.kms.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

import static org.springframework.http.HttpStatus.UNPROCESSABLE_CONTENT;

@RestControllerAdvice
public class RestExceptionHandler {

    /// Konstruktor mit _package private_ für _Spring_.
    RestExceptionHandler() {
    }

    /// [ExceptionHandler], wenn eine Kasse gesucht wird, aber nicht vorhanden ist.
    /// @param ex Die zugehörige [NotFoundException].
    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleNotFoundException(final NotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /// [ExceptionHandler], wenn eine Kasse mit gleicher Bezeichnung bereits existiert.
    /// @param ex Die zugehörige [KasseExistsException].
    @ExceptionHandler(KasseExistsException.class)
    public ProblemDetail handleKasseExistsException(final KasseExistsException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    /// [ExceptionHandler], wenn ungültige Argumente übergeben wurden.
    /// @param ex Die zugehörige [IllegalArgumentException].
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentException(final IllegalArgumentException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /// [ExceptionHandler], wenn Validierungsfehler auftreten.
    /// @param ex Die zugehörige [MethodArgumentNotValidException].
    @ExceptionHandler
    ProblemDetail onConstraintViolations(final MethodArgumentNotValidException ex) {
        final var detailMessages = ex.getDetailMessageArguments();
        final var detail = ((String) detailMessages[1]).replace(", and ", ", ");
        return ProblemDetail.forStatusAndDetail(UNPROCESSABLE_CONTENT, detail);
    }
}
