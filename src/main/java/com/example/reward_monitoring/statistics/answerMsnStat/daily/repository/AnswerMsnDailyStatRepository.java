package com.example.reward_monitoring.statistics.answerMsnStat.daily.repository;


import com.example.reward_monitoring.mission.answerMsn.entity.AnswerMsn;
import com.example.reward_monitoring.statistics.answerMsnStat.daily.entity.AnswerMsnDailyStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AnswerMsnDailyStatRepository extends JpaRepository<AnswerMsnDailyStat,Integer> {

    @Query("SELECT a FROM AnswerMsnDailyStat a WHERE a.partDate > :startAt")
    public List<AnswerMsnDailyStat> findByStartAt(LocalDate startAt);

    @Query("SELECT a FROM AnswerMsnDailyStat a WHERE a.partDate < :endAt")
    public List<AnswerMsnDailyStat> findByEndAt(LocalDate endAt);

    @Query("SELECT a FROM AnswerMsnDailyStat a WHERE a.partDate BETWEEN :startAt AND :endAt")
    public List<AnswerMsnDailyStat> findByBothAt(@Param("startAt") LocalDate startAt, @Param("endAt") LocalDate endAt);


    public List<AnswerMsnDailyStat> findByAdvertiser_Advertiser(String advertiser);

    public List<AnswerMsnDailyStat> findByServer_ServerUrl(String url);

    public List<AnswerMsnDailyStat> findByMediaCompany_CompanyName(String companyName);

    @Query("SELECT a FROM AnswerMsnDailyStat a WHERE a.answerMsn.idx = :idx AND a.partDate BETWEEN :startAt AND :endAt")
    public List<AnswerMsnDailyStat> findByMsnIdx(@Param("idx") int idx,@Param("endAt") LocalDate endAt, @Param("startAt") LocalDate startAt);

    @Query("SELECT a FROM AnswerMsnDailyStat a WHERE a.answerMsn.idx = :idx")
    public List<AnswerMsnDailyStat> findByMsnIdx_(@Param("idx") int idx);

    @Query("SELECT a FROM AnswerMsnDailyStat a WHERE a.partDate= :startAt")
    public List<AnswerMsnDailyStat> findByDate(@Param("startAt") LocalDate startAt);

    @Query("SELECT a FROM AnswerMsnDailyStat a WHERE  a.partDate BETWEEN :past AND :currentTime")
    public List<AnswerMsnDailyStat> findMonth(LocalDate currentTime, LocalDate past);
}
