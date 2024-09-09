package com.example.reward_monitoring.general.advertiser.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AdvertiserSearchDto {

    @Schema(description = "시작일")
    private LocalDate startDate;
    @Schema(description = "종료일")
    private LocalDate endDate;
    @Schema(description = "활성여부")
    private Boolean isActive;
    @Schema(description = "이름")
    private String advertiser;
}
