package ru.practicum.exeption;

public class ErrorResponse {

    private final String status;
    private final String reason;
    private final String message;

    public ErrorResponse(String status, String reason, String message) {
        this.status = status;
        this.reason = reason;
        this.message = message;
    }
}
