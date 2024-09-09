package com.example.reward_monitoring.general.userServer.dto;

import com.example.reward_monitoring.general.userServer.entity.Server;
import lombok.Getter;

@Getter
public class ServerReadDto {
    private String serverName;
    private String serverUrl;

    public Server toEntity(){
        return Server.builder()
                .serverName(serverName)
                .serverUrl(serverUrl)
                .build();
    }
}
