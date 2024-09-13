package com.example.reward_monitoring.mission.searchMsn.dto;


import com.example.reward_monitoring.general.advertiser.entity.Advertiser;
import com.example.reward_monitoring.mission.searchMsn.entity.SearchMsn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@Getter
@Setter
public class SearchMsnReadDto {

    @Schema(description = "미션 기본 수량", example = "50")
    private Integer missionDefaultQty;
    @Schema(description = "미션 데일리 캡", example = "50")
    private Integer missionDailyCap;
    @Schema(description = "미션 노출 순서", example = "100")
    private Integer missionExpOrder;
    @Schema(description = "광고주", example = "원픽")
    private String advertiser;
    @Column(name = "advertiser_details" )
    private String advertiserDetails;
    @Column(name = "mission_title",nullable = false)
    private String missionTitle;
    @Schema(description = "미션 상세 제목", example = "아주 단단한 무쇠웍")
    private String missionDetailTitle;
    @Schema(description = "미션 내용", example = "상세페이지 하단에 구매 추가정보 클릭후 상품번호 앞 5자리를 입력해주세요.")
    private String missionContent;
    @Schema(description = "미션 시작일시", example = "2024-09-04 15:00:00")
    private ZonedDateTime startAtMsn;
    @Schema(description = "미션 종료일시", example = "2024-09-13 23:40:00")
    private ZonedDateTime endAtMsn;
    @Schema(description = "데일리캡 시작일시", example = "2024-09-04 ")
    private LocalDate startAtCap;
    @Schema(description = "데일리캡 종료일시", example = "2024-09-13")
    private LocalDate endAtCap;
    @Schema(description = "중복 참여 가능여부", example = "true")
    private Boolean dupParticipation;
    @Schema(description = "미션 사용여부", example = "true")
    private Boolean  missionActive;
    @Schema(description = "미션 노출여부", example = "true")
    private Boolean  missionExposure;
    @Schema(description = "재참여 가능일", example = "1")
    private Integer reEngagementDay;
    @Schema(description = "참여 제외할 매체 IDX,1|2|3|4|5형식으로 넣어야함", example = "1|2|3|4|5",nullable = true)
    private String exceptMedia;
    @Schema(description = "미션 URL", example = "www.abc.com",nullable = true)
    private String msnUrl;
    @Schema(description = "검색 키워드",example = "검색키워드")
    private String searchKeyword;
    @Schema(description = "미션 정답", example = "정답1")
    private String msnAnswer;
    @Schema(description = "미션 정답2", example = "정답2")
    private String msnAnswer2;
    @Schema(description = "미션 데이터 타입")
    private Boolean dataType;
    @Schema(description = "이미지 파일")
    private byte[] imageData;
    @Schema(description = "이미지 파일명")
    private String imageName;
    public SearchMsn toEntity(Advertiser advertiserEntity){

        return SearchMsn.builder()
                .missionDefaultQty(missionDefaultQty)
                .missionDailyCap(missionDailyCap)
                .advertiser(advertiserEntity)
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
                .exceptMedia(exceptMedia)
                .msnUrl(msnUrl)
                .searchKeyword(searchKeyword)
                .msnAnswer(msnAnswer)
                .msnAnswer2(msnAnswer2)
                .dataType(dataType)
                .imageData(imageData)
                .imageName(imageName)
                .build();
    }
}
