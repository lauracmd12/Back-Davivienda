package com.davivienda.surveyplatform.service.impl;

import com.davivienda.surveyplatform.dto.auth.*;
import com.davivienda.surveyplatform.entity.LoginEntity;
import com.davivienda.surveyplatform.repository.GetRepository;
import com.davivienda.surveyplatform.repository.InsertRepository;
import com.davivienda.surveyplatform.repository.QuestionRepository;
import com.davivienda.surveyplatform.repository.SurveyRepository;
import com.davivienda.surveyplatform.service.AuthService;
import com.davivienda.surveyplatform.service.dto.ResponseLoginDto;
import com.davivienda.surveyplatform.service.dto.ResponseServiceDto;
import com.davivienda.surveyplatform.util.ApiUtils;
import com.davivienda.surveyplatform.util.enums.HttpResponseCodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import com.davivienda.surveyplatform.entity.QuestionEntity;
import com.davivienda.surveyplatform.entity.SurveyEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private GetRepository getRepository;

    @Autowired
    private InsertRepository insertRepository;

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private QuestionRepository questionRepository;


    @Autowired
    private ApiUtils apiUtils;

    @Override
    public ResponseServiceDto login(LoginRequest request) {

        System.out.println("🚀 LOGIN - Iniciando proceso para: " + request.getEmail());

        //Consultar en bd usario
        Optional<LoginEntity> consultLogin = getRepository.findByEmailAndPasswordHash
                (request.getEmail(), request.getPassword());

        if (consultLogin.isPresent()) {
            LoginEntity user = consultLogin.get(); // ← Extrae el objeto primero

            ResponseLoginDto responseLogin = new ResponseLoginDto();
            responseLogin.setId(user.getId());
            responseLogin.setEmail(user.getEmail());
            responseLogin.setPassword(user.getPasswordHash());

            return apiUtils.buildResponseServiceDto(HttpResponseCodes.OK.getCode(), "Usuario encontrado", responseLogin);

        } else {
            return apiUtils.buildResponseServiceDto(HttpResponseCodes.BUSINESS_MISTAKE.getCode(), "Usuario y/o contraseña incorrectos", null);
        }
    }

    @Override
    public ResponseServiceDto register(RegisterRequest request) {

        System.out.println("🚀 REGISTRO - Iniciando proceso para: " + request.getEmail());
        if (request.getEmail() != null){
            Optional<LoginEntity> exitsEmail = getRepository.findByEmail
                    (request.getEmail());
            if (exitsEmail.isPresent()) {
                return apiUtils.buildResponseServiceDto(HttpResponseCodes.BAD_REQUEST.getCode(), "Correo ya existe", null);
            }
            else{
                if (request.getPassword() != null && request.getName() != null){
                    LoginEntity login = new LoginEntity();
                    login.setName(request.getName());
                    login.setEmail(request.getEmail());
                    login.setPasswordHash(request.getPassword());
                    login.setCompany(request.getCompany());
                    login.setCreatedAt(LocalDateTime.now());
                    login.setUpdatedAt(null);
                    insertRepository.save(login);
                    return apiUtils.buildResponseServiceDto(HttpResponseCodes.CREATED.getCode(), "Usuario creado exitosamente", null);
                } else {
                    return apiUtils.buildResponseServiceDto(HttpResponseCodes.BUSINESS_MISTAKE.getCode(), "Parametros de entrada invalidos", null);
                }
            }
        }
         else{
            return apiUtils.buildResponseServiceDto(HttpResponseCodes.BUSINESS_MISTAKE.getCode(), "Parametros de entrada invalidos", null);
        }
    }

    @Override
    @Transactional
    public ResponseServiceDto createSurvey(CreateSurveyRequest request, UUID createdBy) {

        System.out.println("🚀 CREATE SURVEY - Iniciando proceso para usuario: " + createdBy);

        try {
            // Validaciones básicas
            if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
                return apiUtils.buildResponseServiceDto(
                        HttpResponseCodes.BUSINESS_MISTAKE.getCode(),
                        "El título es obligatorio",
                        null
                );
            }

            if (request.getQuestions() == null || request.getQuestions().isEmpty()) {
                return apiUtils.buildResponseServiceDto(
                        HttpResponseCodes.BUSINESS_MISTAKE.getCode(),
                        "Debe incluir al menos una pregunta",
                        null
                );
            }

            // Crear la encuesta
            SurveyEntity survey = SurveyEntity.builder()
                    .id(UUID.randomUUID())
                    .title(request.getTitle().trim())
                    .description(request.getDescription() != null ? request.getDescription().trim() : null)
                    .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                    .isPublic(request.getIsPublic() != null ? request.getIsPublic() : true)
                    .createdBy(createdBy)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            // Guardar survey primero
            SurveyEntity savedSurvey = surveyRepository.save(survey);
            System.out.println("✅ Survey guardado con ID: " + savedSurvey.getId());

            // Crear y guardar las preguntas
            List<QuestionEntity> questions = request.getQuestions().stream()
                    .map(questionRequest -> QuestionEntity.builder()
                            .id(UUID.randomUUID())
                            .survey(savedSurvey)
                            .surveyId(savedSurvey.getId())
                            .type(questionRequest.getType())
                            .title(questionRequest.getTitle().trim())
                            .description(questionRequest.getDescription() != null ?
                                    questionRequest.getDescription().trim() : null)
                            .required(questionRequest.getRequired() != null ? questionRequest.getRequired() : false)
                            .options(questionRequest.getOptions())
                            .orderIndex(questionRequest.getOrder())
                            .createdAt(LocalDateTime.now())
                            .build())
                    .collect(Collectors.toList());

            // Guardar todas las preguntas
            List<QuestionEntity> savedQuestions = questionRepository.saveAll(questions);
            System.out.println("✅ Preguntas guardadas: " + savedQuestions.size());

            // Preparar respuesta
            SurveyResponse surveyResponse = convertToSurveyResponse(savedSurvey, savedQuestions);

            return apiUtils.buildResponseServiceDto(
                    HttpResponseCodes.CREATED.getCode(),
                    "Encuesta creada exitosamente",
                    surveyResponse
            );

        } catch (Exception e) {
            System.err.println("❌ Error al crear encuesta: " + e.getMessage());
            e.printStackTrace();
            return apiUtils.buildResponseServiceDto(
                    HttpResponseCodes.INTERNAL_SERVER_ERROR.getCode(),
                    "Error interno al crear la encuesta",
                    null
            );
        }
    }

    @Override
    public ResponseServiceDto getSurveysByUser(UUID userId) {

        System.out.println("🚀 GET SURVEYS BY USER - Consultando para usuario: " + userId);

        try {
            if (userId == null) {
                return apiUtils.buildResponseServiceDto(
                        HttpResponseCodes.BUSINESS_MISTAKE.getCode(),
                        "ID de usuario requerido",
                        null
                );
            }

            List<SurveyEntity> surveys = surveyRepository.findByCreatedByOrderByCreatedAtDesc(userId);
            System.out.println("✅ Encontradas " + surveys.size() + " encuestas");

            List<SurveyResponse> surveyResponses = surveys.stream()
                    .map(survey -> {
                        // Obtener preguntas para cada survey
                        List<QuestionEntity> questions = questionRepository.findBySurveyIdOrderByOrderIndex(survey.getId());
                        return convertToSurveyResponse(survey, questions);
                    })
                    .collect(Collectors.toList());

            return apiUtils.buildResponseServiceDto(
                    HttpResponseCodes.OK.getCode(),
                    "Encuestas obtenidas exitosamente",
                    surveyResponses
            );

        } catch (Exception e) {
            System.err.println("❌ Error al obtener encuestas: " + e.getMessage());
            return apiUtils.buildResponseServiceDto(
                    HttpResponseCodes.INTERNAL_SERVER_ERROR.getCode(),
                    "Error al obtener las encuestas",
                    null
            );
        }
    }

    /* @Override
     public ResponseServiceDto getSurveyById(UUID surveyId) {

         System.out.println("🚀 GET SURVEY BY ID - Consultando survey: " + surveyId);

         try {
             if (surveyId == null) {
                 return apiUtils.buildResponseServiceDto(
                         HttpResponseCodes.BUSINESS_MISTAKE.getCode(),
                         "ID de encuesta requerido",
                         null
                 );
             }

             Optional<SurveyEntity> surveyOpt = surveyRepository.findById(surveyId);

             if (surveyOpt.isPresent()) {
                 SurveyEntity survey = surveyOpt.get();
                 List<QuestionEntity> questions = questionRepository.findBySurveyIdOrderByOrderIndex(surveyId);

                 SurveyResponse surveyResponse = convertToSurveyResponse(survey, questions);

                 return apiUtils.buildResponseServiceDto(
                         HttpResponseCodes.OK.getCode(),
                         "Encuesta encontrada",
                         surveyResponse
                 );
             } else {
                 return apiUtils.buildResponseServiceDto(
                         HttpResponseCodes.BUSINESS_MISTAKE.getCode(),
                         "Encuesta no encontrada",
                         null
                 );
             }

         } catch (Exception e) {
             System.err.println("❌ Error al obtener encuesta: " + e.getMessage());
             return apiUtils.buildResponseServiceDto(
                     HttpResponseCodes.INTERNAL_SERVER_ERROR.getCode(),
                     "Error al obtener la encuesta",
                     null
             );
         }
     }

     @Override
     public ResponseServiceDto getActiveSurveysByUser(UUID userId) {

         System.out.println("🚀 GET ACTIVE SURVEYS - Consultando encuestas activas para: " + userId);

         try {
             if (userId == null) {
                 return apiUtils.buildResponseServiceDto(
                         HttpResponseCodes.BUSINESS_MISTAKE.getCode(),
                         "ID de usuario requerido",
                         null
                 );
             }

             List<SurveyEntity> surveys = surveyRepository.findByCreatedByAndIsActiveOrderByCreatedAtDesc(userId, true);

             List<SurveyResponse> surveyResponses = surveys.stream()
                     .map(survey -> {
                         List<QuestionEntity> questions = questionRepository.findBySurveyIdOrderByOrderIndex(survey.getId());
                         return convertToSurveyResponse(survey, questions);
                     })
                     .collect(Collectors.toList());

             return apiUtils.buildResponseServiceDto(
                     HttpResponseCodes.OK.getCode(),
                     "Encuestas activas obtenidas",
                     surveyResponses
             );

         } catch (Exception e) {
             System.err.println("❌ Error al obtener encuestas activas: " + e.getMessage());
             return apiUtils.buildResponseServiceDto(
                     HttpResponseCodes.INTERNAL_SERVER_ERROR.getCode(),
                     "Error al obtener encuestas activas",
                     null
             );
         }
     }

     @Override
     public ResponseServiceDto getPublicSurveys() {

         System.out.println("🚀 GET PUBLIC SURVEYS - Consultando encuestas públicas");

         try {
             List<SurveyEntity> surveys = surveyRepository.findByIsPublicAndIsActiveOrderByCreatedAtDesc(true, true);

             List<SurveyResponse> surveyResponses = surveys.stream()
                     .map(survey -> {
                         List<QuestionEntity> questions = questionRepository.findBySurveyIdOrderByOrderIndex(survey.getId());
                         return convertToSurveyResponse(survey, questions);
                     })
                     .collect(Collectors.toList());

             return apiUtils.buildResponseServiceDto(
                     HttpResponseCodes.OK.getCode(),
                     "Encuestas públicas obtenidas",
                     surveyResponses
             );

         } catch (Exception e) {
             System.err.println("❌ Error al obtener encuestas públicas: " + e.getMessage());
             return apiUtils.buildResponseServiceDto(
                     HttpResponseCodes.INTERNAL_SERVER_ERROR.getCode(),
                     "Error al obtener encuestas públicas",
                     null
             );
         }
     }

     @Override
     @Transactional
     public ResponseServiceDto updateSurvey(UUID surveyId, CreateSurveyRequest request, UUID userId) {

         System.out.println("🚀 UPDATE SURVEY - Actualizando survey: " + surveyId + " por usuario: " + userId);

         try {
             Optional<SurveyEntity> surveyOpt = surveyRepository.findById(surveyId);

             if (!surveyOpt.isPresent()) {
                 return apiUtils.buildResponseServiceDto(
                         HttpResponseCodes.BUSINESS_MISTAKE.getCode(),
                         "Encuesta no encontrada",
                         null
                 );
             }

             SurveyEntity survey = surveyOpt.get();

             // Verificar que el usuario sea el creador
             if (!survey.getCreatedBy().equals(userId)) {
                 return apiUtils.buildResponseServiceDto(
                         HttpResponseCodes.BAD_REQUEST.getCode(),
                         "No tienes permisos para modificar esta encuesta",
                         null
                 );
             }

             // Actualizar datos del survey
             survey.setTitle(request.getTitle().trim());
             survey.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
             survey.setIsActive(request.getIsActive() != null ? request.getIsActive() : survey.getIsActive());
             survey.setIsPublic(request.getIsPublic() != null ? request.getIsPublic() : survey.getIsPublic());
             survey.setUpdatedAt(LocalDateTime.now());

             // Eliminar preguntas existentes
             questionRepository.deleteBySurveyId(surveyId);

             // Crear nuevas preguntas
             List<QuestionEntity> newQuestions = request.getQuestions().stream()
                     .map(questionRequest -> QuestionEntity.builder()
                             .survey(survey)
                             .surveyId(survey.getId())
                             .type(questionRequest.getType())
                             .title(questionRequest.getTitle().trim())
                             .description(questionRequest.getDescription() != null ?
                                     questionRequest.getDescription().trim() : null)
                             .required(questionRequest.getRequired() != null ? questionRequest.getRequired() : false)
                             .options(questionRequest.getOptions())
                             .orderIndex(questionRequest.getOrder())
                             .createdAt(LocalDateTime.now())
                             .build())
                     .collect(Collectors.toList());

             // Guardar todo
             SurveyEntity savedSurvey = surveyRepository.save(survey);
             List<QuestionEntity> savedQuestions = questionRepository.saveAll(newQuestions);

             SurveyResponse surveyResponse = convertToSurveyResponse(savedSurvey, savedQuestions);

             return apiUtils.buildResponseServiceDto(
                     HttpResponseCodes.OK.getCode(),
                     "Encuesta actualizada exitosamente",
                     surveyResponse
             );

         } catch (Exception e) {
             System.err.println("❌ Error al actualizar encuesta: " + e.getMessage());
             return apiUtils.buildResponseServiceDto(
                     HttpResponseCodes.INTERNAL_SERVER_ERROR.getCode(),
                     "Error al actualizar la encuesta",
                     null
             );
         }
     }

     @Override
     public ResponseServiceDto deleteSurvey(UUID surveyId) {


         try {
             Optional<SurveyEntity> surveyOpt = surveyRepository.findById(surveyId);

             if (!surveyOpt.isPresent()) {
                 return apiUtils.buildResponseServiceDto(
                         HttpResponseCodes.BUSINESS_MISTAKE.getCode(),
                         "Encuesta no encontrada",
                         null
                 );
             }

             SurveyEntity survey = surveyOpt.get();

             survey.setIsActive(false);
             survey.setUpdatedAt(LocalDateTime.now());
             surveyRepository.save(survey);

             return apiUtils.buildResponseServiceDto(
                     HttpResponseCodes.OK.getCode(),
                     "Encuesta eliminada exitosamente",
                     null
             );

         } catch (Exception e) {
             System.err.println("❌ Error al eliminar encuesta: " + e.getMessage());
             return apiUtils.buildResponseServiceDto(
                     HttpResponseCodes.INTERNAL_SERVER_ERROR.getCode(),
                     "Error al eliminar la encuesta",
                     null
             );
         }
     }

     @Override
     public ResponseServiceDto getSurveyStats(UUID userId) {

         System.out.println("🚀 GET SURVEY STATS - Consultando estadísticas para: " + userId);

         try {
             if (userId == null) {
                 return apiUtils.buildResponseServiceDto(
                         HttpResponseCodes.BUSINESS_MISTAKE.getCode(),
                         "ID de usuario requerido",
                         null
                 );
             }

             long totalSurveys = surveyRepository.countByCreatedBy(userId);
             long activeSurveys = surveyRepository.countByCreatedByAndIsActive(userId, true);
             long inactiveSurveys = totalSurveys - activeSurveys;

             Map<String, Long> stats = new HashMap<>();
             stats.put("totalSurveys", totalSurveys);
             stats.put("activeSurveys", activeSurveys);
             stats.put("inactiveSurveys", inactiveSurveys);

             return apiUtils.buildResponseServiceDto(
                     HttpResponseCodes.OK.getCode(),
                     "Estadísticas obtenidas",
                     stats
             );

         } catch (Exception e) {
             System.err.println("❌ Error al obtener estadísticas: " + e.getMessage());
             return apiUtils.buildResponseServiceDto(
                     HttpResponseCodes.INTERNAL_SERVER_ERROR.getCode(),
                     "Error al obtener estadísticas",
                     null
             );
         }
     }
 */
    // Método helper para convertir entidades a DTOs
    private SurveyResponse convertToSurveyResponse(SurveyEntity survey, List<QuestionEntity> questions) {

        List<QuestionResponse> questionResponses = questions.stream()
                .map(question -> QuestionResponse.builder()
                        .id(question.getId())
                        .surveyId(question.getSurveyId())
                        .type(question.getType())
                        .title(question.getTitle())
                        .description(question.getDescription())
                        .required(question.getRequired())
                        .options(question.getOptions())
                        .order(question.getOrderIndex())
                        .createdAt(question.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return SurveyResponse.builder()
                .id(survey.getId())
                .title(survey.getTitle())
                .description(survey.getDescription())
                .isActive(survey.getIsActive())
                .isPublic(survey.getIsPublic())
                .createdBy(survey.getCreatedBy())
                .createdAt(survey.getCreatedAt())
                .updatedAt(survey.getUpdatedAt())
                .questions(questionResponses)
                .totalQuestions(questionResponses.size())
                .build();
    }

}