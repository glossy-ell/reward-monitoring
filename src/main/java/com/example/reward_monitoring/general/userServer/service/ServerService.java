package com.example.reward_monitoring.general.userServer.service;

import com.example.reward_monitoring.general.member.entity.Member;
import com.example.reward_monitoring.general.userServer.dto.ServerEditDto;
import com.example.reward_monitoring.general.userServer.dto.ServerReadDto;
import com.example.reward_monitoring.general.userServer.dto.ServerSearchDto;
import com.example.reward_monitoring.general.userServer.entity.Server;
import com.example.reward_monitoring.general.userServer.repository.ServerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServerService {


    @Autowired
    private ServerRepository serverRepository;

    public Server delete(int idx) {
        return null;
    }

    public Server edit(int idx, ServerEditDto dto) {
        Server server = serverRepository.findByIdx(idx);
        if(server == null)
            return null;
        if(dto.getServerName()!=null)
            server.setServerName(dto.getServerName());
        if(dto.getServerUrl()!=null)
            server.setServerUrl(dto.getServerUrl());
        if(dto.getIsActive()!=null){
            boolean bool = dto.getIsActive();
            server.setActive(bool);
        }
         serverRepository.save(server);
        return server;
    }

    public Server add(ServerReadDto dto) {
        return dto.toEntity();
    }

    public Server getServer(int idx) {
        return serverRepository.findByIdx(idx);
    }

    public List<Server> getServers() {
        return serverRepository.findAll();
    }

    public List<Server> searchMember(ServerSearchDto dto) {
        return null;
    }

}
