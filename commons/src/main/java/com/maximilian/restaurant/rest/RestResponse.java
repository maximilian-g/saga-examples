package com.maximilian.restaurant.rest;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class RestResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;
    private String message;
    private boolean success;

    public RestResponse(LocalDateTime timestamp, String message) {
        this.timestamp = timestamp;
        this.message = message;
    }

    public RestResponse(String message) {
        this();
        this.message = message;
    }

    public RestResponse() {
        timestamp = LocalDateTime.now();
        success = true;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "RestResponse{" +
                "timestamp=" + timestamp +
                ", message='" + message + '\'' +
                ", success=" + success +
                '}';
    }
}
