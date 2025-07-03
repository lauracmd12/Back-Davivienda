package com.davivienda.surveyplatform.repository;

import com.davivienda.surveyplatform.entity.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuestionRepository extends JpaRepository<QuestionEntity, String> {

    // Buscar preguntas por survey ordenadas
    List<QuestionEntity> findBySurveyIdOrderByOrderIndex(UUID surveyId);

    // Buscar preguntas por survey y tipo
    List<QuestionEntity> findBySurveyIdAndTypeOrderByOrderIndex(UUID surveyId, String type);

    // Contar preguntas por survey
    long countBySurveyId(UUID surveyId);

    // Buscar preguntas obligatorias de un survey
    List<QuestionEntity> findBySurveyIdAndRequiredOrderByOrderIndex(UUID surveyId, Boolean required);

    // Eliminar todas las preguntas de un survey
    void deleteBySurveyId(UUID surveyId);

    // Obtener el máximo order_index de un survey - CORREGIDO
    @Query("SELECT COALESCE(MAX(q.orderIndex), 0) FROM QuestionEntity q WHERE q.surveyId = :surveyId")
    Integer getMaxOrderIndexBySurvey(@Param("surveyId") UUID surveyId);

    // Buscar preguntas con opciones - CORREGIDO
    @Query("SELECT q FROM QuestionEntity q WHERE q.surveyId = :surveyId AND SIZE(q.options) > 0 ORDER BY q.orderIndex")
    List<QuestionEntity> findQuestionsWithOptions(@Param("surveyId") UUID surveyId);
}