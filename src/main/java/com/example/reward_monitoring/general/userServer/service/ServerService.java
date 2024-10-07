package com.example.reward_monitoring.general.userServer.service;


import com.example.reward_monitoring.general.userServer.RandomKeyGenerator;
import com.example.reward_monitoring.general.userServer.dto.ServerEditDto;
import com.example.reward_monitoring.general.userServer.dto.ServerReadDto;
import com.example.reward_monitoring.general.userServer.dto.ServerSearchDto;
import com.example.reward_monitoring.general.userServer.entity.Server;
import com.example.reward_monitoring.general.userServer.repository.ServerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
            server.setActive(dto.getIsActive());
        }
        if(dto.getServerKey()!=null){
            server.setServerKey(dto.getServerKey());
        }
        if(dto.getMemo()!=null){
            server.setMemo(dto.getMemo());
        }
        serverRepository.save(server);
        return server;
    }

    public Server add(ServerReadDto dto) {
        String key = RandomKeyGenerator.generateRandomKey(15);
        if(dto.getServerKey() ==null )
            dto.setServerKey(key);
        return dto.toEntity();
    }

    public Server getServer(int idx) {
        return serverRepository.findByIdx(idx);
    }

    public List<Server> getServers() {
        return serverRepository.findAll();
    }


    public List<Server> searchServer(ServerSearchDto dto) {
        List<Server> target_server_url;
        List<Server> target_server_name;
        List<Server> target_is_active;
        List<Server> result = new ArrayList<>();


        if(dto.getIsActive() != null){
            target_is_active = serverRepository.findByIsActive(dto.getIsActive());
            result.addAll(target_is_active);
        }
        if(dto.getServerUrl()!=null){
            target_server_url = serverRepository.findByServerUrl(dto.getServerUrl());
            if(result.isEmpty())
                result.addAll(target_server_url);
            else{
                result.retainAll(target_server_url);
            }
        }

        if(dto.getServerName()!=null && !dto.getServerName().isEmpty()){
            target_server_name = serverRepository.findByServerName(dto.getServerName());
            if(result.isEmpty())
                result.addAll(target_server_name);
            else{
                result.retainAll(target_server_name);
            }
        }
        return result.stream().distinct().collect(Collectors.toList());
    }
}
