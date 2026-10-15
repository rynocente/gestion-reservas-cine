package com.mitocode.cine.booking.exception;

import com.mitocode.cine.booking.exception.NoSeatsAvailableException;
import com.mitocode.cine.booking.exception.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NoSeatsAvailableException.class)
    protected ResponseEntity<Object> handleNoSeatsAvailable(
            NoSeatsAvailableException ex, WebRequest request) {

        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problem.setTitle("No Seats Available");

        return handleExceptionInternal(ex, problem, new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY, request);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    protected ResponseEntity<Object> handleResourceNotFound(
            ResourceNotFoundException ex, WebRequest request) {

        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Resource Not Found");

        return handleExceptionInternal(ex, problem, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(InvalidBookingStateException.class)
    protected ResponseEntity<Object> handleInvalidState(InvalidBookingStateException ex, WebRequest request) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problem.setTitle("Transición de Estado Inválida");

        return handleExceptionInternal(ex, problem, new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY, request);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleGeneric(Exception ex, WebRequest request) {

        var problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
        problem.setTitle("Internal Server Error");

        return handleExceptionInternal(ex, problem, new HttpHeaders(),
                HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}