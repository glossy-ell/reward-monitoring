package com.example.reward_monitoring.statistics.searchMsn.daily.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class SearchMsnDailyStatSearchDto {


    @Schema(description = "서버URL",example = "https://www.abc.com")
    private String url;
    @Schema(description = "광고주",example = "시크릿 K")
    private String advertiser;
    @Schema(description = "매체사",example = "오케이캐시백(QA0")
    private String mediacompany;
    @Schema(description = "검색 시작일", example = "2024-09-11")
    private LocalDate startAt;
    @Schema(description = "검색 종료일", example = "2024-09-11")
    private LocalDate endAt;

    @Schema(description =" 정렬방식", example = "3")
    private String sOrder;
}
