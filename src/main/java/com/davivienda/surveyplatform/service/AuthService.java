package com.davivienda.surveyplatform.service;

import com.davivienda.surveyplatform.dto.auth.CreateSurveyRequest;
import com.davivienda.surveyplatform.dto.auth.LoginRequest;
import com.davivienda.surveyplatform.dto.auth.RegisterRequest;
import com.davivienda.surveyplatform.service.dto.ResponseServiceDto;
import java.util.UUID;
public interface AuthService {
    ResponseServiceDto login(LoginRequest loginRequest);

    ResponseServiceDto register(RegisterRequest registerRequest);

    ResponseServiceDto createSurvey(CreateSurveyRequest request, UUID createdBy);

    ResponseServiceDto getSurveysByUser(UUID userId);

    /*ResponseServiceDto getSurveyById(UUID surveyId);

    ResponseServiceDto getActiveSurveysByUser(UUID userId);

    ResponseServiceDto getPublicSurveys();

    ResponseServiceDto updateSurvey(UUID surveyId, CreateSurveyRequest request, UUID userId);

    ResponseServiceDto deleteSurvey(UUID surveyId);

    ResponseServiceDto getSurveyStats(UUID userId);*/
}
