package com.davivienda.surveyplatform.controller;

import com.davivienda.surveyplatform.dto.auth.*;
import com.davivienda.surveyplatform.service.AuthService;
import com.davivienda.surveyplatform.service.dto.ResponseServiceDto;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.davivienda.surveyplatform.dto.auth.CreateSurveyRequest;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    /**
     * POST /auth/login
     * Endpoint para iniciar sesión
     */
    @PostMapping("/login")
    public ResponseEntity<ResponseServiceDto> login(@Valid @RequestBody LoginRequest loginRequest) {

        ResponseServiceDto response = authService.login(loginRequest);

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseServiceDto> register(@Valid @RequestBody RegisterRequest registerRequest) {

        ResponseServiceDto response = authService.register(registerRequest);

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // Crear encuesta
    @PostMapping("/create")
    public ResponseEntity<ResponseServiceDto> createSurvey(
            @Valid @RequestBody CreateSurveyRequest request,
            @RequestHeader("User-Id") String userIdHeader) {

        System.out.println("🎯 CREATE SURVEY - Request recibido para usuario: " + userIdHeader);

        try {
            UUID userId = UUID.fromString(userIdHeader);
            ResponseServiceDto response = authService.createSurvey(request, userId);

            if (response.getStatus() == 201) {
                return ResponseEntity.status(201).body(response);
            } else if (response.getStatus() == 400) {
                return ResponseEntity.badRequest().body(response);
            } else {
                return ResponseEntity.status(500).body(response);
            }

        } catch (IllegalArgumentException e) {
            System.err.println("❌ UUID inválido: " + userIdHeader);
            ResponseServiceDto errorResponse = new ResponseServiceDto("ID de usuario inválido", 400, null);
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            System.err.println("❌ Error en crear encuesta: " + e.getMessage());
            ResponseServiceDto errorResponse = new ResponseServiceDto("Error interno del servidor", 500, null);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    // Obtener encuestas por usuario
    @GetMapping("/my-surveys")
    public ResponseEntity<ResponseServiceDto> getMySurveys(
            @RequestHeader("User-Id") String userIdHeader) {

        System.out.println("🎯 GET MY SURVEYS - Request para usuario: " + userIdHeader);

        try {
            UUID userId = UUID.fromString(userIdHeader);
            ResponseServiceDto response = authService.getSurveysByUser(userId);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            System.err.println("❌ UUID inválido: " + userIdHeader);
            ResponseServiceDto errorResponse = new ResponseServiceDto("ID de usuario inválido", 400, null);
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            System.err.println("❌ Error en obtener encuestas: " + e.getMessage());
            ResponseServiceDto errorResponse = new ResponseServiceDto("Error interno del servidor", 500, null);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /*// Obtener encuesta por ID
    @GetMapping("/{surveyId}")
    public ResponseEntity<ResponseServiceDto> getSurveyById(@PathVariable String surveyId) {

        System.out.println("🎯 GET SURVEY BY ID - Request para survey: " + surveyId);

        try {
            UUID id = UUID.fromString(surveyId);
            ResponseServiceDto response = authService.getSurveyById(id);

            if (response.getStatus() == 200) {
                return ResponseEntity.ok(response);
            } else if (response.getStatus() == 404) {
                return ResponseEntity.status(404).body(response);
            } else {
                return ResponseEntity.status(500).body(response);
            }

        } catch (IllegalArgumentException e) {
            System.err.println("❌ UUID inválido: " + surveyId);
            ResponseServiceDto errorResponse = new ResponseServiceDto("ID de encuesta inválido", 400, null);
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            System.err.println("❌ Error en obtener encuesta: " + e.getMessage());
            ResponseServiceDto errorResponse = new ResponseServiceDto("Error interno del servidor", 500, null);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    // Obtener encuestas activas del usuario
    @GetMapping("/my-surveys/active")
    public ResponseEntity<ResponseServiceDto> getMyActiveSurveys(
            @RequestHeader("User-Id") String userIdHeader) {

        System.out.println("🎯 GET ACTIVE SURVEYS - Request para usuario: " + userIdHeader);

        try {
            UUID userId = UUID.fromString(userIdHeader);
            ResponseServiceDto response = authService.getActiveSurveysByUser(userId);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            System.err.println("❌ UUID inválido: " + userIdHeader);
            ResponseServiceDto errorResponse = new ResponseServiceDto("ID de usuario inválido", 400, null);
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            System.err.println("❌ Error en obtener encuestas activas: " + e.getMessage());
            ResponseServiceDto errorResponse = new ResponseServiceDto("Error interno del servidor", 500, null);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    // Obtener encuestas públicas
    @GetMapping("/public")
    public ResponseEntity<ResponseServiceDto> getPublicSurveys() {

        System.out.println("🎯 GET PUBLIC SURVEYS - Request recibido");

        try {
            ResponseServiceDto response = authService.getPublicSurveys();
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ Error en obtener encuestas públicas: " + e.getMessage());
            ResponseServiceDto errorResponse = new ResponseServiceDto("Error interno del servidor", 500, null);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    // Actualizar encuesta
    @PutMapping("/{surveyId}")
    public ResponseEntity<ResponseServiceDto> updateSurvey(
            @PathVariable String surveyId,
            @Valid @RequestBody CreateSurveyRequest request,
            @RequestHeader("User-Id") String userIdHeader) {

        System.out.println("🎯 UPDATE SURVEY - Request para survey: " + surveyId + " por usuario: " + userIdHeader);

        try {
            UUID id = UUID.fromString(surveyId);
            UUID userId = UUID.fromString(userIdHeader);

            ResponseServiceDto response = authService.updateSurvey(id, request, userId);

            if (response.getStatus() == 200) {
                return ResponseEntity.ok(response);
            } else if (response.getStatus() == 403) {
                return ResponseEntity.status(403).body(response);
            } else if (response.getStatus() == 404) {
                return ResponseEntity.status(404).body(response);
            } else {
                return ResponseEntity.status(500).body(response);
            }

        } catch (IllegalArgumentException e) {
            System.err.println("❌ UUID inválido - Survey: " + surveyId + " User: " + userIdHeader);
            ResponseServiceDto errorResponse = new ResponseServiceDto("IDs inválidos", 400, null);
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            System.err.println("❌ Error en actualizar encuesta: " + e.getMessage());
            ResponseServiceDto errorResponse = new ResponseServiceDto("Error interno del servidor", 500, null);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    // Eliminar encuesta (soft delete)
    @DeleteMapping("/deletesurvey")
    public ResponseEntity<ResponseServiceDto> deleteSurvey(
    @RequestParam (value = "id") String id) {
            ResponseServiceDto response = authService.deleteSurvey(id);

            if (response.getStatus() == 200) {
                return ResponseEntity.ok(response);
            } else if (response.getStatus() == 400) {
                return ResponseEntity.status(400).body(response);
            } else if (response.getStatus() == 206) {
                return ResponseEntity.status(206).body(response);
            } else {
                return ResponseEntity.status(500).body(response);
            }
    }

    // Obtener estadísticas del usuario
    @GetMapping("/my-surveys/stats")
    public ResponseEntity<ResponseServiceDto> getMySurveyStats(
            @RequestHeader("User-Id") String userIdHeader) {

        System.out.println("🎯 GET SURVEY STATS - Request para usuario: " + userIdHeader);

        try {
            UUID userId = UUID.fromString(userIdHeader);
            ResponseServiceDto response = authService.getSurveyStats(userId);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            System.err.println("❌ UUID inválido: " + userIdHeader);
            ResponseServiceDto errorResponse = new ResponseServiceDto("ID de usuario inválido", 400, null);
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            System.err.println("❌ Error en obtener estadísticas: " + e.getMessage());
            ResponseServiceDto errorResponse = new ResponseServiceDto("Error interno del servidor", 500, null);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    // Endpoint simple para testing
    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("Survey API funcionando correctamente ✅");
    }
*/
}
