package com.example.gorest.model;

public class ErrorResponse {
    private String field;
    private String message;

    public ErrorResponse() {}

    public String getField() { return field; }
    public void setField(String field) { this.field = field; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
