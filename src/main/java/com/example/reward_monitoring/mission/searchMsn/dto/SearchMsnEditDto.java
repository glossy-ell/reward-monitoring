package com.example.reward_monitoring.mission.searchMsn.dto;

import lombok.Getter;

import java.time.LocalDate;
import java.time.ZonedDateTime;


@Getter
public class SearchMsnEditDto {
    private Integer missionDefaultQty;
    private Integer missionDailyCap;
    private String missionTitle;
    private String searchKeyword;
    private ZonedDateTime startAtMsn;
    private ZonedDateTime endAtMsn;
    private LocalDate startAtCap;
    private LocalDate endAtCap;
    private Boolean missionActive;
    private Boolean missionExposure;
    private Boolean dupParticipation;
    private Integer reEngagementDay;
}
