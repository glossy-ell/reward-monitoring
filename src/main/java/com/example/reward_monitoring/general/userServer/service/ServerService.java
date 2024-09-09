package com.example.reward_monitoring.general.userServer.service;

import com.example.reward_monitoring.general.member.dto.MemberSearchDto;
import com.example.reward_monitoring.general.member.entity.Member;
import com.example.reward_monitoring.general.userServer.dto.ServerEditDto;
import com.example.reward_monitoring.general.userServer.dto.ServerReadDto;
import com.example.reward_monitoring.general.userServer.dto.ServerSearchDto;
import com.example.reward_monitoring.general.userServer.entity.Server;
import com.example.reward_monitoring.general.userServer.repository.ServerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
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
        List<Server> target_server_url=null;
        List<Server> target_server_name=null;
        List<Server> target_is_active = null;
        List<Server> result=null;


        if(dto.getIsActive() != null){
            target_is_active = serverRepository.findByIsActive(dto.getIsActive());
        }
        if(dto.getServerUrl()!=null){
            target_server_url = serverRepository.findByServerUrl(dto.getServerUrl());
        }

        if(dto.getServerName()!=null){
            target_server_name = serverRepository.findByServerName(dto.getServerName());
        }

        if(target_is_active!=null) {
            result = new ArrayList<>(target_is_active);
            if(target_server_name!=null)
                result.retainAll(target_server_name);
            else if(target_server_url!=null)
                result.retainAll(target_server_url);

        }
        else if(target_server_url !=null){
            result = new ArrayList<>(target_server_url);

        } else if (target_server_name != null) {
            result = new ArrayList<>(target_server_name);
        }

        return result;

    }
}
