package com.example.reward_monitoring.general.mediaCompany.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class MediaCompanySearchDto {
    @Schema(description = "시작일")
    private LocalDate startDate;
    @Schema(description = "종료일")
    private LocalDate endDate;
    @Schema(description = "활성여부")
    private Boolean isActive;
    @Schema(description = "매체사명")
    private String name;
    @Schema(description = "API키")
    private String api;
    @Schema(description = "운영 타입")
    private Boolean operationType;
}
