package com.kaique.transacao_api.infrastructure.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(UnprocessableEntity.class)
    public ResponseEntity<Void> handleUnprocessableEntity(UnprocessableEntity exception) {
        return ResponseEntity.unprocessableEntity().build();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Void> handleBadRequest(HttpMessageNotReadableException exception) {
        return ResponseEntity.badRequest().build();
    }
}