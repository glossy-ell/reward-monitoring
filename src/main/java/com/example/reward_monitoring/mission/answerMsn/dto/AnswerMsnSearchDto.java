package com.example.reward_monitoring.mission.answerMsn.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class AnswerMsnSearchDto {

    @Schema(description = "미션시작시간")
    private LocalDate startAtMsn;

    @Schema(description = "미션종료시간")
    private LocalDate endAtMsn;

    @Schema(description = "미션데일리캡시작")
    private LocalDate startAtCap;

    @Schema(description = "미션데일리캡종료")
    private LocalDate  endAtCap;

    @Schema(description = "중복참여여부")
    private Boolean dupParticipation;

    @Schema(description = "미션상태")
    private Boolean missionActive;

    @Schema(description = "미션 노출여부")
    private Boolean missionExposure;

    @Schema(description = "미션 노출여부")
    private Boolean dataType;

    @Schema(description = "광고주")
    private String advertiser;

    @Schema(description = "광고상세")
    private String advertiserDetails;

    @Schema(description = "미션 제목")
    private String missionTitle;

}
