package com.example.reward_monitoring.mission.answerMsn.dto;

import com.example.reward_monitoring.general.advertiser.entity.Advertiser;
import com.example.reward_monitoring.mission.answerMsn.entity.AnswerMsn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDate;
import java.time.ZonedDateTime;

@Getter
@Setter
public class AnswerMsnReadDto {

    @Schema(description = "미션 기본 수량", example = "50")
    private Integer missionDefaultQty;
    @Schema(description = "미션 데일리 캡", example = "50")
    private Integer missionDailyCap;
    @Schema(description = "광고주", example = "광고주")
    private String advertiser;
    @Schema(description = "광고주 상세", example = "광고주")
    private String advertiserDetail;
    @Schema(description = "미션 제목", example = "무쇠웍")
    private String missionTitle;
    @Schema(description = "미션 제목", example = "5107811272")
    private String missionAnswer;
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

    public AnswerMsn toEntity(Advertiser advertiserEntity){

        return AnswerMsn.builder()
                .missionDefaultQty(missionDefaultQty)
                .missionDailyCap(missionDailyCap)
                .advertiser(advertiserEntity)
                .missionTitle(missionTitle)
                .missionAnswer(missionAnswer)
                .startAtMsn(startAtMsn)
                .endAtMsn(endAtMsn)
                .startAtCap(startAtCap)
                .endAtCap(endAtCap)
                .missionActive(missionActive)
                .missionExposure(missionExposure)
                .dupParticipation(dupParticipation)
                .reEngagementDay(reEngagementDay)
                .build();
    }

}
