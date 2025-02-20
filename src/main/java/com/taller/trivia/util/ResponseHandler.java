package com.taller.trivia.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseHandler<T> {

    public static <T> ResponseEntity<Response<T>> handleResponse(T body) {
        Response<T> errorResponse = new Response<T>(
                HttpStatus.OK.value(),
                body
        );
        return ResponseEntity.ok(errorResponse);
    }


    public static ResponseEntity<ErrorResponse> handleErrorResponse(HttpStatus status, String message, String description) {
        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                message,
                description
        );
        return  ResponseEntity.status(status).body(errorResponse);
    }
}
