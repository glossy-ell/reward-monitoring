package com.example.reward_monitoring.statistics.saveMsn.sum.repository;



import com.example.reward_monitoring.statistics.answerMsnStat.sum.entity.AnswerMsnSumStat;
import com.example.reward_monitoring.statistics.saveMsn.sum.entity.SaveMsnSumStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SaveMsnSumStatRepository extends JpaRepository<SaveMsnSumStat,Integer> {

    @Query("SELECT s FROM SaveMsnSumStat s WHERE s.date > :startAt")
    public List<SaveMsnSumStat> findByStartAt(LocalDate startAt);

    @Query("SELECT s FROM SaveMsnSumStat s WHERE s.date < :endAt")
    public List<SaveMsnSumStat> findByEndAt(LocalDate endAt);

    @Query("SELECT s FROM SaveMsnSumStat s WHERE s.date BETWEEN :startAt AND :endAt")
    public List<SaveMsnSumStat>  findByBothAt(@Param("startAt") LocalDate startAt, @Param("endAt") LocalDate endAt);


    public List<SaveMsnSumStat>  findByAdvertiser_Advertiser(String advertiser);

    public List<SaveMsnSumStat>  findByServer_ServerUrl(String url);

    public List<SaveMsnSumStat>  findByMediaCompany_CompanyName(String companyName);

    @Query("SELECT a FROM AnswerMsnSumStat a WHERE a.date BETWEEN :past AND :currentTime")
    public List<SaveMsnSumStat> findMonth(LocalDate currentTime, LocalDate past);
}
