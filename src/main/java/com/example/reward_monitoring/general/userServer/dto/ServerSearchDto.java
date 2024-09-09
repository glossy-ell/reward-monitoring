package com.example.reward_monitoring.general.userServer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ServerSearchDto {
    @Schema(description = "활성여부")
    private Boolean isActive;
    @Schema(description = "서버URL")
    private String serverUrl;
    @Schema(description = "서버명")
    private String serverName;
}
