package org.example.exception;

import org.example.entity.response.ErrorResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException ex) {
        ErrorResponse response = new ErrorResponse(400, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CurrencyNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCurrencyNotFound(CurrencyNotFoundException ex) {
        ErrorResponse response = new ErrorResponse(404, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CurrencyServiceUnavailableException.class)
    public ResponseEntity<Object> handleCurrencyServiceUnavailable(CurrencyServiceUnavailableException ex) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Retry-after", String.valueOf(Duration.ofHours(1).getSeconds()));
        ErrorResponse response = new ErrorResponse(503, ex.getMessage());
        return new ResponseEntity<>(response, headers, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
