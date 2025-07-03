package com.davivienda.surveyplatform.service.dto;

public class ResponseServiceDto<T> {
    private String message;
    private int status;
    private T data;

    // Constructores
    public ResponseServiceDto() {}

    public ResponseServiceDto(String message, int status) {
        this.message = message;
        this.status = status;
    }

    public ResponseServiceDto(String message, int status, T data) {
        this.message = message;
        this.status = status;
        this.data = data;
    }

    // Getters y Setters
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    // Builder pattern manual
    public static <T> ResponseServiceDtoBuilder<T> builder() {
        return new ResponseServiceDtoBuilder<>();
    }

    public static class ResponseServiceDtoBuilder<T> {
        private String message;
        private int status;
        private T data;

        public ResponseServiceDtoBuilder<T> message(String message) {
            this.message = message;
            return this;
        }

        public ResponseServiceDtoBuilder<T> status(int status) {
            this.status = status;
            return this;
        }

        public ResponseServiceDtoBuilder<T> data(T data) {
            this.data = data;
            return this;
        }

        public ResponseServiceDto<T> build() {
            return new ResponseServiceDto<>(message, status, data);
        }
    }
}