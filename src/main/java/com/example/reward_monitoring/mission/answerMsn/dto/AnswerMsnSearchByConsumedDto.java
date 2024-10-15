package com.example.reward_monitoring.mission.answerMsn.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class AnswerMsnSearchByConsumedDto {

    @Schema(description = "광고주", example = "시크릿 K")
    private String advertiser;

    @Schema(description = "서버 Url", example = "www.abc.com")
    private String serverUrl;

    @Schema(description = "미션 제목", example = "무쇠웍")
    private String missionTitle;

    @Schema(description = "광고주 상세", example = "82652333318")
    private String advertiserDetails;
}
