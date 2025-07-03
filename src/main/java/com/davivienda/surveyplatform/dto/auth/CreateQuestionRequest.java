package com.davivienda.surveyplatform.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateQuestionRequest {

    @NotBlank(message = "El tipo de pregunta es obligatorio")
    private String type;

    @NotBlank(message = "El título de la pregunta es obligatorio")
    @Size(max = 1000, message = "El título no puede exceder 1000 caracteres")
    private String title;

    @Size(max = 2000, message = "La descripción no puede exceder 2000 caracteres")
    private String description;

    private Boolean required = false;

    private List<String> options;

    @NotNull(message = "El orden es obligatorio")
    private Integer order;
}
