package com.davivienda.surveyplatform.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class ResponseLoginDto {
    private UUID id;
    private String email;
    private String password;
}
