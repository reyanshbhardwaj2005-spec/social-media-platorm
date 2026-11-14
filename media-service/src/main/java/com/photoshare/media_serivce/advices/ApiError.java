package com.photoshare.media_serivce.advices;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Data
@RestControllerAdvice
public class ApiError {

    private LocalDateTime timeStamp;
    private HttpStatus statusCode;
    private String error;

    public ApiError(HttpStatus statusCode, String error) {
        this.statusCode = statusCode;
        this.error = error;
    }

    public ApiError(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public ApiError() {

    }
}
