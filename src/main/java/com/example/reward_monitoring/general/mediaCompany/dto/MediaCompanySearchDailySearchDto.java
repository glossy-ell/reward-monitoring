package com.example.reward_monitoring.general.mediaCompany.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class MediaCompanySearchDailySearchDto {

    @Schema(description = "검색 시작일", example = "2024-09-11")
    private LocalDate startAt;
    @Schema(description = "검색 종료일", example = "2024-09-11")
    private LocalDate endAt;
}
