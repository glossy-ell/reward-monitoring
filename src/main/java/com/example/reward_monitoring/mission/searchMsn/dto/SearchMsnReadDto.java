package com.example.reward_monitoring.mission.searchMsn.dto;


import com.example.reward_monitoring.mission.searchMsn.entity.SearchMsn;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@Getter
@Setter
public class SearchMsnReadDto {

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

    public SearchMsn toEntity(){

        return SearchMsn.builder()
                .missionDefaultQty(missionDefaultQty)
                .missionDailyCap(missionDailyCap)
                .missionTitle(missionTitle)
                .searchKeyword(searchKeyword)
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
