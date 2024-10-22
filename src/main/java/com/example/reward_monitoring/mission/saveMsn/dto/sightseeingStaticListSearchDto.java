package com.example.reward_monitoring.mission.saveMsn.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class sightseeingStaticListSearchDto {


    @Schema(description = "미션시작시간")
    private LocalDate startDate;

    @Schema(description = "미션종료시간")
    private LocalDate endDate;

    @Schema(description = "서버 url")
    private String url;

    @Schema(description = "서버 url")
    private int idx;


}
