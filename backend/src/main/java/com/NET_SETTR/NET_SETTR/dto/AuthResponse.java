package com.NET_SETTR.NET_SETTR.dto;

public class AuthResponse {
    private String status;
    private String message;

    public AuthResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() { return status; }
    public String getMessage() { return message; }
}
