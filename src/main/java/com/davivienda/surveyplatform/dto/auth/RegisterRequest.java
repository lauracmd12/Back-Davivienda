// Register Request DTO
package com.davivienda.surveyplatform.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "El nombre es requerido")
    @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
    private String name;

    @NotBlank(message = "El email es requerido")
    @Email(message = "Formato de email inválido")
    @Size(max = 255, message = "El email no puede exceder 255 caracteres")
    private String email;

    @NotBlank(message = "La contraseña es requerida")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    @Size(max = 255, message = "La empresa no puede exceder 255 caracteres")
    private String company;

    // Constructors
    public RegisterRequest() {}

    public RegisterRequest(String name, String email, String password, String company) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.company = company;
    }

}