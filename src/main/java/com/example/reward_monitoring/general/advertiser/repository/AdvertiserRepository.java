package com.example.reward_monitoring.general.advertiser.repository;

import com.example.reward_monitoring.general.advertiser.entity.Advertiser;

import com.example.reward_monitoring.general.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.ZonedDateTime;
import java.util.List;

public interface AdvertiserRepository extends JpaRepository<Advertiser,Integer> {

    public Advertiser findByIdx(int idx);


    @Query("SELECT a FROM Advertiser a WHERE a.createdAt > :startDate")
    List<Advertiser> findByStartDate(ZonedDateTime startDate);

    @Query("SELECT a FROM Advertiser a WHERE a.createdAt < :endDate")
    List<Advertiser> findByEndDate(ZonedDateTime endDate);

    @Query("SELECT a FROM Advertiser a WHERE a.createdAt BETWEEN :startDate AND :endDate")
    List<Advertiser> findByBothDate(ZonedDateTime startDate, ZonedDateTime endDate);

    @Query("SELECT a FROM Advertiser a WHERE a.isActive = :isActive")
    List<Advertiser> findByIsActive(Boolean isActive);

    @Query("SELECT a FROM Advertiser a WHERE a.advertiser LIKE %:keyword% ")
    List<Advertiser> findByAdvertiser(String keyword);

    @Query("SELECT a FROM Advertiser a WHERE a.advertiser = :keyword")
    Advertiser findByAdvertiser_(String keyword);
}
