package com.example.reward_monitoring.general.userServer.repository;

import com.example.reward_monitoring.general.userServer.entity.Server;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ServerRepository extends JpaRepository<Server,Integer> {
    public Server findByIdx(int idx);

    @Query("SELECT s FROM Server s WHERE s.isActive = :isActive")
    public List<Server> findByIsActive(boolean isActive);

    @Query("SELECT s FROM Server s WHERE s.serverUrl LIKE %:keyword% ")
    public List<Server> findByServerUrl(@Param("keyword") String keyword);

    @Query("SELECT s FROM Server s WHERE s.serverName LIKE %:keyword% ")
    public List<Server> findByServerName(@Param("keyword") String keyword);

    @Query("SELECT s FROM Server s WHERE s.serverUrl = :keyword")
    public Server findByServerUrl_(@Param("keyword")String keyword);
}
