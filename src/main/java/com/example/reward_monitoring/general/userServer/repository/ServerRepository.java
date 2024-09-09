package com.example.reward_monitoring.general.userServer.repository;


import com.example.reward_monitoring.general.member.entity.Member;
import com.example.reward_monitoring.general.userServer.entity.Server;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServerRepository extends JpaRepository<Server,Integer> {
    public Server findByIdx(int idx);

}
