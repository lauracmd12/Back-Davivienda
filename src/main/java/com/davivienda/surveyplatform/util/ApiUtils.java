package com.davivienda.surveyplatform.util;

import com.davivienda.surveyplatform.service.dto.ResponseServiceDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ApiUtils {

    public <T> ResponseServiceDto<T> buildResponseServiceDto(int status, String message, T data) {
        return ResponseServiceDto.<T>builder()
                .status(status)
                .message(message)
                .data(data)
                .build();
    }
}
