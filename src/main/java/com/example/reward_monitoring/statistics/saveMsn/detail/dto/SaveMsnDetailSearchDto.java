package com.example.reward_monitoring.statistics.saveMsn.detail.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class SaveMsnDetailSearchDto {
    @Schema(description = "서버 URL", example = "https://www.abc.com")
    private String url;
    @Schema(description = "검색 시작일", example = "2024-09-11")
    private LocalDate startAt;
    @Schema(description = "검색 종료일", example = "2024-09-11")
    private LocalDate endAt;
    @Schema(description = "매체사명", example = "오케이캐쉬백(QA)")
    private String mediacompany;
    @Schema(description = "매체사명", example = "flase")
    private Boolean isAbuse;
    @Schema(description = "응답확인", example = "true")
    private Boolean response;
    @Schema(description = "광고주", example = "시크릿 K")
    private String advertiser;
    @Schema(description =" 정답미션 idx", example = "3")
    private Integer idx;
}
