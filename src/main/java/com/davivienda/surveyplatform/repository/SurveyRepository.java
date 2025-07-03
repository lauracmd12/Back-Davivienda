package com.davivienda.surveyplatform.repository;

import com.davivienda.surveyplatform.entity.SurveyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SurveyRepository extends JpaRepository<SurveyEntity, UUID> {

    // Buscar surveys por usuario creador
    List<SurveyEntity> findByCreatedByOrderByCreatedAtDesc(UUID createdBy);

    // Buscar surveys por usuario con paginación
    Page<SurveyEntity> findByCreatedByOrderByCreatedAtDesc(UUID createdBy, Pageable pageable);

    // Buscar surveys activos por usuario
    List<SurveyEntity> findByCreatedByAndIsActiveOrderByCreatedAtDesc(UUID createdBy, Boolean isActive);

    // Buscar surveys públicos y activos
    List<SurveyEntity> findByIsPublicAndIsActiveOrderByCreatedAtDesc(Boolean isPublic, Boolean isActive);

    // Buscar survey con sus preguntas - CORREGIDO
    @Query("SELECT DISTINCT s FROM SurveyEntity s LEFT JOIN FETCH s.questions q WHERE s.id = :surveyId ORDER BY q.orderIndex")
    Optional<SurveyEntity> findByIdWithQuestions(@Param("surveyId") UUID surveyId);

    // Buscar surveys por usuario con sus preguntas - CORREGIDO
    @Query("SELECT DISTINCT s FROM SurveyEntity s LEFT JOIN FETCH s.questions q WHERE s.createdBy = :createdBy ORDER BY s.createdAt DESC, q.orderIndex")
    List<SurveyEntity> findByCreatedByWithQuestions(@Param("createdBy") UUID createdBy);

    // Contar surveys por usuario
    long countByCreatedBy(UUID createdBy);

    // Contar surveys activos por usuario
    long countByCreatedByAndIsActive(UUID createdBy, Boolean isActive);

    // Buscar por título (contiene)
    List<SurveyEntity> findByTitleContainingIgnoreCaseAndCreatedByOrderByCreatedAtDesc(String title, UUID createdBy);

    // Buscar surveys por rango de fechas - CORREGIDO
    @Query("SELECT s FROM SurveyEntity s WHERE s.createdBy = :createdBy AND s.createdAt BETWEEN :startDate AND :endDate ORDER BY s.createdAt DESC")
    List<SurveyEntity> findByCreatedByAndDateRange(
            @Param("createdBy") UUID createdBy,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // Estadísticas de surveys por usuario
    @Query(value = """
        SELECT 
            is_active,
            is_public,
            COUNT(*) as count
        FROM [dbo].[surveys] 
        WHERE created_by = :createdBy 
        GROUP BY is_active, is_public
        """, nativeQuery = true)
    List<Object[]> getSurveyStatsByUser(@Param("createdBy") UUID createdBy);
}