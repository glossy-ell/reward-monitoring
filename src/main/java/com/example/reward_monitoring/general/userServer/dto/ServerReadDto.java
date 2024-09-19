package com.example.reward_monitoring.general.userServer.dto;

import com.example.reward_monitoring.general.userServer.entity.Server;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServerReadDto {
    @Schema(description = "서버명",example = "오케이캐시백")
    private String serverName;
    @Schema(description = "서버 URL",example = "https://ocb.srk.co.kr")
    private String serverUrl;
    @Schema(description = "서버 사용여부",example = "true,false")
    private Boolean isActive;
    @Schema(description = "서버 통신 키",example = "1234")
    private String serverKey;
    @Schema(description = "관리자 메모")
    private String memo;

    public Server toEntity(){
        return Server.builder()
                .serverName(serverName)
                .serverUrl(serverUrl)
                .isActive(isActive)
                .serverKey(serverKey)
                .memo(memo)
                .build();
    }
}
