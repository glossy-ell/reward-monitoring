package com.example.reward_monitoring.general.advertiser.repository;

import com.example.reward_monitoring.general.advertiser.entity.Advertiser;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AdvertiserRepository extends JpaRepository<Advertiser,Integer> {

    public Advertiser findByIdx(int idx);


    @Query("SELECT a FROM Advertiser a WHERE a.createdAt > :startDate")
    List<Advertiser> findByStartDate(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT a FROM Advertiser a WHERE a.createdAt < :endDate")
    List<Advertiser> findByEndDate(@Param("endDate") LocalDateTime endDate);

    @Query("SELECT a FROM Advertiser a WHERE a.createdAt BETWEEN :startDate AND :endDate")
    List<Advertiser> findByBothDate(@Param("startDate")LocalDateTime startDate,@Param("endDate") LocalDateTime endDate);

    @Query("SELECT a FROM Advertiser a WHERE a.isActive = :isActive")
    List<Advertiser> findByIsActive(@Param("isActive") Boolean isActive);

    @Query("SELECT a FROM Advertiser a WHERE a.advertiser LIKE %:keyword% ")
    List<Advertiser> findByAdvertiser(@Param("keyword") String keyword);

    @Query("SELECT a FROM Advertiser a WHERE a.advertiser = :keyword")
    Advertiser findByAdvertiser_(@Param("keyword") String keyword);
}
