package com.courcework.pharmacyclient.api.error;

public record ApiError(int status, String error, String message) {
}
