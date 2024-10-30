package com.example.reward_monitoring.mission.answerMsn.dto;



import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Setter
@Getter
public class AnswerMsnEditDto {

    @Schema(description = "미션 기본 수량", example = "50")
    private Integer missionDefaultQty;

    @Schema(description = "미션 데일리 캡", example = "50")
    private Integer missionDailyCap;

    @Schema(description = "미션 노출 순서", example = "50")
    private Integer missionExpOrder;

    @Schema(description = "광고주", example = "원픽")
    private String advertiser;

    @Schema(description = "광고주 상세", example = "82652333318")
    private String advertiserDetails = "";


    @Schema(description = "미션 제목", example = "무쇠웍")
    private String missionTitle;

    @Schema(description = "미션 상세 제목", example = "무쇠웍")
    private String missionDetailTitle = "";

    @Schema(description = "미션 정답", example = "5107811272")
    private String missionAnswer = "";

    @Schema(description = "미션 내용", example = "상세페이지 하단에 구매 추가정보 클릭후 상품번호 앞 5자리를 입력해주세요.")
    private String missionContent;


    private String startAtMsnDate; // 날짜 필드
    private String startTime;  // 시간 필드

    @Schema(description = "미션 시작일시", example = "2024-09-04 15:00:00")
    private LocalDateTime startAtMsn;

    private String endAtMsnDate; // 날짜 필드
    private String endTime;  // 시간 필드

    @Schema(description = "미션 종료일시", example = "2024-09-13 23:40:00")
    private LocalDateTime endAtMsn;

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

    @Schema(description = "제외 매체사 IDX(구분자 |)", example = "1|2|3|4|",nullable = true)
    private String exceptMedia;

    @Schema(description = "미션 URL1", example = "www.abc.com",nullable = true)
    private String msnUrl1;

    @Schema(description = "미션 URL2", example = "www.abc.com",nullable = true)
    private String  msnUrl2;

    @Schema(description = "미션 URL3", example = "www.abc.com",nullable = true)
    private String msnUrl3;

    @Schema(description = "미션 URL4", example = "www.abc.com",nullable = true)
    private String msnUrl4;

    @Schema(description = "미션 URL5", example = "www.abc.com",nullable = true)
    private String msnUrl5;

    @Schema(description = "미션 URL6", example = "www.abc.com",nullable = true)
    private String msnUrl6;

    @Schema(description = "미션 URL7", example = "www.abc.com",nullable = true)
    private String msnUrl7;

    @Schema(description = "미션 URL8", example = "www.abc.com",nullable = true)
    private String msnUrl8;

    @Schema(description = "미션 URL9", example = "www.abc.com",nullable = true)
    private String msnUrl9;

    @Schema(description = "미션 URL10", example = "www.abc.com",nullable = true)
    private String msnUrl10;

    @Schema(description = "미션 데이터 타입", example = "false = 삭제 데이터 , true = 정상 데이터")
    private Boolean dataType;

    @Hidden
    private byte[] imageData;

    @Hidden
    private String imageName;




}
