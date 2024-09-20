package com.example.reward_monitoring.mission.saveMsn.dto;


import com.example.reward_monitoring.general.advertiser.entity.Advertiser;
import com.example.reward_monitoring.general.userServer.entity.Server;
import com.example.reward_monitoring.mission.saveMsn.entity.SaveMsn;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@Getter
@Setter
public class SaveMsnReadDto {

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
    @Schema(description = "미션 최종 URL", example = "www.abc.com",nullable = true)
    private String msnFinalUrl;
    @Schema(description = "검색 키워드", example="검색 키워드")
    private String searchKeyword;
    @Schema(description = "미션 데이터 타입" , example = "true")
    private Boolean dataType;
    @Hidden
    private byte[] imageData;
    @Hidden
    private String imageName;

    @Schema(description = "서버url", example = "https://ocb.srk.co.kr")
    private String url;

    public SaveMsn toEntity(Advertiser advertiserEntity, Server serverEntity){

        return SaveMsn.builder()
                .missionDefaultQty(missionDefaultQty)
                .missionDailyCap(missionDailyCap)
                .advertiser(advertiserEntity)
                .server(serverEntity)
                .advertiserDetails(advertiserDetails)
                .missionTitle(missionTitle)
                .missionDetailTitle(missionDetailTitle)
                .missionContent(missionContent)
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
                .msnFinalUrl(msnFinalUrl)
                .searchKeyword(searchKeyword)
                .dataType(dataType)
                .imageData(imageData)
                .imageName(imageName)
                .build();
    }
}
