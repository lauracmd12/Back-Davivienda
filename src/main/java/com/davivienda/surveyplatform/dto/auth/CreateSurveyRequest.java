package com.davivienda.surveyplatform.dto.auth;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateSurveyRequest {

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 500, message = "El título no puede exceder 500 caracteres")
    private String title;

    @Size(max = 2000, message = "La descripción no puede exceder 2000 caracteres")
    private String description;

    private Boolean isActive = true;

    private Boolean isPublic = true;

    @NotEmpty(message = "Debe incluir al menos una pregunta")
    @Valid
    private List<CreateQuestionRequest> questions;
}