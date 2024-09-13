package com.example.reward_monitoring.mission.saveMsn.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class SaveMsnActiveDto {

    @Schema(description = "미션 사용 여부")
    boolean isActive;
}
