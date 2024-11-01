package com.example.reward_monitoring.statistics.saveMsn.sum.repository;


import com.example.reward_monitoring.statistics.saveMsn.sum.entity.SaveMsnSumStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SaveMsnSumStatRepository extends JpaRepository<SaveMsnSumStat,Integer> {

    @Query("SELECT s FROM SaveMsnSumStat s WHERE s.date >= :startAt")
    public List<SaveMsnSumStat> findByStartAt(@Param("startAt") LocalDate startAt);

    @Query("SELECT s FROM SaveMsnSumStat s WHERE s.date <= :endAt")
    public List<SaveMsnSumStat> findByEndAt(@Param("endAt") LocalDate endAt);

    @Query("SELECT s FROM SaveMsnSumStat s WHERE s.date BETWEEN :startAt AND :endAt")
    public List<SaveMsnSumStat>  findByBothAt(@Param("startAt") LocalDate startAt, @Param("endAt") LocalDate endAt);

    public List<SaveMsnSumStat>  findByAdvertiser_Advertiser( String advertiser);

    public List<SaveMsnSumStat>  findByServer_ServerUrl(String url);

    public List<SaveMsnSumStat>  findByMediaCompany_CompanyName(String companyName);

    @Query("SELECT s FROM SaveMsnSumStat s WHERE s.date BETWEEN :past AND :currentTime")
    public List<SaveMsnSumStat> findMonth(@Param("currentTime")LocalDate currentTime, @Param("past") LocalDate past);

    @Query("SELECT s FROM SaveMsnSumStat s WHERE s.mediaCompany.idx = :aidx")
    public List<SaveMsnSumStat> findByMediaCompanyIdx(@Param("aidx") int aidx);

    @Query("SELECT s FROM SaveMsnSumStat s WHERE (s.date BETWEEN :past AND :currentTime) AND (s.mediaCompany.idx = :aidx)")
    public List<SaveMsnSumStat> findMonthByAffiliate(@Param("currentTime") LocalDate currentTime,@Param("past") LocalDate past,@Param("aidx") int aidx);

}
