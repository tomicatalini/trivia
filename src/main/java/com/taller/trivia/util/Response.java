package com.taller.trivia.util;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> {
    private int status;
    private T result;
    private LocalDateTime timestamp;

    public Response(int status, T result) {
        this.status = status;
        this.result = result;
        this.timestamp = LocalDateTime.now();
    }
}
