package com.example.reward_monitoring.general.mediaCompany.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MediaCompanySightseeingCurrentSearchDto {

    @Schema(description = "미션 제목")
    private String missionTitle;
}
