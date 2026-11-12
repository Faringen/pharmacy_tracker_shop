package com.courcework.controller;

import com.courcework.repository.ErrorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private ResponseEntity<ErrorRepository> build(HttpStatus status, String message){
        return ResponseEntity.status(status)
                .body(ErrorRepository.of(status.value(), status.getReasonPhrase(), message));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorRepository> handleNotFound(EntityNotFoundException e){
        return build(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorRepository> handleBadArgument(IllegalArgumentException e){
        return build(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorRepository> handleMissingParam(MissingServletRequestParameterException e){
        return build(HttpStatus.BAD_REQUEST, "Не указан обязательный параметр: " + e.getParameterName());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorRepository> handleTypeMissing(MethodArgumentTypeMismatchException e){
        return build(HttpStatus.BAD_REQUEST, "Некорректное значение параметра: " + e.getName());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorRepository> handleUnsuspected(Exception e){
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера");
    }


}
