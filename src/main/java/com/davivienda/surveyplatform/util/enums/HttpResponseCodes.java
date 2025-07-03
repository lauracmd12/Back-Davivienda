package com.davivienda.surveyplatform.util.enums;

public enum HttpResponseCodes {
    OK(200),
    CREATED(201),
    BUSINESS_MISTAKE(206),
    BAD_REQUEST(400),
    INTERNAL_SERVER_ERROR(500);

    private final int code;
    HttpResponseCodes(int code) {this.code = code;}

    public int getCode() {
        return code;
    }
}
