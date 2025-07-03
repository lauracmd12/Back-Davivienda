package com.davivienda.surveyplatform.dto.auth;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponse {
    private UUID id;
    private UUID surveyId;
    private String type;
    private String title;
    private String description;
    private Boolean required;
    private List<String> options;
    private Integer order;
    private LocalDateTime createdAt;
}
