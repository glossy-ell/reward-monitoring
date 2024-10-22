package com.example.reward_monitoring.statistics.answerMsnStat.sum.repository;


import com.example.reward_monitoring.statistics.answerMsnStat.daily.entity.AnswerMsnDailyStat;
import com.example.reward_monitoring.statistics.answerMsnStat.sum.entity.AnswerMsnSumStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AnswerMsnSumStatRepository extends JpaRepository<AnswerMsnSumStat,Integer> {

    @Query("SELECT a FROM AnswerMsnSumStat a WHERE a.date > :startAt")
    public List<AnswerMsnSumStat> findByStartAt(LocalDate startAt);

    @Query("SELECT a FROM AnswerMsnSumStat a WHERE a.date < :endAt")
    public List<AnswerMsnSumStat> findByEndAt(LocalDate endAt);

    @Query("SELECT a FROM AnswerMsnSumStat a WHERE a.date BETWEEN :startAt AND :endAt")
    public List<AnswerMsnSumStat>  findByBothAt(@Param("startAt") LocalDate startAt, @Param("endAt") LocalDate endAt);


    public List<AnswerMsnSumStat>  findByAdvertiser_Advertiser(String advertiser);

    public List<AnswerMsnSumStat>  findByServer_ServerUrl(String url);

    public List<AnswerMsnSumStat>  findByMediaCompany_CompanyName(String companyName);

    @Query("SELECT a FROM AnswerMsnSumStat a WHERE a.date BETWEEN :past AND :currentTime")
    public List<AnswerMsnSumStat> findMonth(LocalDate currentTime, LocalDate past);
}
