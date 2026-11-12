package com.courcework.repository;

import java.time.Instant;

public record ErrorRepository(int status, String error, String message, Instant timestamp) {
    public static ErrorRepository of(int status, String error, String message){
        return new ErrorRepository(status, error, message, Instant.now());
    }
}
