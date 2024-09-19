package com.example.reward_monitoring.statistics.answerMsnStat.daily.dto;

import com.example.reward_monitoring.general.advertiser.entity.Advertiser;
import com.example.reward_monitoring.general.mediaCompany.entity.MediaCompany;
import com.example.reward_monitoring.general.userServer.entity.Server;
import com.example.reward_monitoring.mission.answerMsn.entity.AnswerMsn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;

@Getter
public class AnswerMsnDailyStatSearchDto {


    @Schema(description = "서버URL",example = "https://www.abc.com")
    String url;
    @Schema(description = "광고주",example = "시크릿 K")
    String advertiser;
    @Schema(description = "매체사",example = "오케이캐시백(QA0")
    String mediacompany;
    @Schema(description = "검색 시작일", example = "2024-09-11")
    private LocalDate startAt;
    @Schema(description = "검색 종료일", example = "2024-09-11")
    private LocalDate endAt;

}
