package com.example.reward_monitoring.mission.answerMsn.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class AnswerMsnExposeDto {

    @Schema(description = "미션 노출 여부")
    private boolean isExpose;
}
