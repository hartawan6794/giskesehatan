package com.example.giskesehatan.Models;

public class ApiResponse<T> {
    private boolean status;
    private String message;
    private T result;

    public boolean isStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getResult() {
        return result;
    }
}