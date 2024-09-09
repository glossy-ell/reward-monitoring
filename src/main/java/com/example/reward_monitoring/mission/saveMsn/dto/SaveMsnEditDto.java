package com.example.reward_monitoring.mission.saveMsn.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDate;
import java.time.ZonedDateTime;


@Getter
public class SaveMsnEditDto {

    @Schema(description = "미션 기본 수량", example = "50")
    private Integer missionDefaultQty;
    @Schema(description = "미션 데일리 캡", example = "50")
    private Integer missionDailyCap;
    @Schema(description = "미션 제목", example = "무쇠웍")
    private String missionTitle;
    @Schema(description = "검색 키워드", example = "비산동 치과 큐플란트의원")
    private String searchKeyword;
    @Schema(description = "미션 시작일시", example = "2024-09-04 15:00:00")
    private ZonedDateTime startAtMsn;
    @Schema(description = "미션 종료일시", example = "2024-09-13 23:40:00")
    private ZonedDateTime endAtMsn;
    @Schema(description = "데일리캡 시작일시", example = "2024-09-04 ")
    private LocalDate startAtCap;
    @Schema(description = "데일리캡 종료일시", example = "2024-09-13")
    private LocalDate endAtCap;
    @Schema(description = "미션 사용여부", example = "true")
    private Boolean missionActive;
    @Schema(description = "미션 노출여부", example = "true")
    private Boolean missionExposure;
    @Schema(description = "중복 참여 가능여부", example = "true")
    private Boolean dupParticipation;
    @Schema(description = "재참여 가능일", example = "1")
    private Integer reEngagementDay;


}
