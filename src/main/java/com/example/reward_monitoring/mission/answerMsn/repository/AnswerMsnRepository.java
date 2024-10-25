package com.example.reward_monitoring.mission.answerMsn.repository;

import com.example.reward_monitoring.mission.answerMsn.entity.AnswerMsn;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

public interface AnswerMsnRepository extends JpaRepository<AnswerMsn,Integer> {
    public AnswerMsn findByIdx(int idx);

    @Query("SELECT a FROM AnswerMsn a WHERE a.startAtMsn > :startDate")
    public List<AnswerMsn> findByStartDate(ZonedDateTime startDate);

    @Query("SELECT a FROM AnswerMsn a WHERE a.endAtMsn < :endDate")
    public List<AnswerMsn> findByEndDate(@Param("endDate") ZonedDateTime endDate);

    @Query("SELECT a FROM AnswerMsn a WHERE a.startAtMsn > :startDate AND a.endAtMsn < :endDate")
    public List<AnswerMsn> findByBothDate(@Param("startDate") ZonedDateTime startDate, @Param("endDate") ZonedDateTime endDate);

    @Query("SELECT a FROM AnswerMsn a WHERE a.startAtCap > :startCap")
    public List<AnswerMsn> findByStartAtCap(LocalDate startCap);

    @Query("SELECT a FROM AnswerMsn a WHERE a.endAtCap < :endCap")
    public List<AnswerMsn> findByEndAtCap(LocalDate endCap);

    @Query("SELECT a FROM AnswerMsn a WHERE a.startAtCap > :startCap AND a.endAtCap < :endCap")
    public List<AnswerMsn> findByBothCap(@Param("startCap") LocalDate startCap, @Param("endCap") LocalDate endCap);

    @Query("SELECT a FROM AnswerMsn a WHERE a.missionActive = :missionActive")
    public List<AnswerMsn> findByMissionActive(boolean missionActive);

    @Query("SELECT a FROM AnswerMsn a WHERE a.dataType = :dataType")
    public List<AnswerMsn> findByDataType(boolean dataType);

    @Query("SELECT a FROM AnswerMsn a WHERE a.dupParticipation = :dupParticipation")
    public List<AnswerMsn> findByDupParticipation(boolean dupParticipation);

    @Query("SELECT a FROM AnswerMsn a WHERE a.missionExposure = :missionExposure")
    public List<AnswerMsn> findByMissionExposure(boolean missionExposure);

    @Query("SELECT a FROM AnswerMsn a WHERE a.advertiser.advertiser LIKE %:keyword%")
    public List<AnswerMsn> findByAdvertiser(@Param("keyword") String keyword);

    @Query("SELECT a FROM AnswerMsn a WHERE a.server.serverUrl LIKE %:keyword%")
    public List<AnswerMsn> findByServer(@Param("keyword") String keyword);

    @Query("SELECT a FROM AnswerMsn a  WHERE a.advertiserDetails LIKE %:keyword% ")
    public List<AnswerMsn> findByAdvertiserDetails(String keyword);

    @Query("SELECT a FROM AnswerMsn a  WHERE a.missionTitle LIKE %:keyword% ")
    public List<AnswerMsn> findByMissionTitle(String keyword);

    @Query("SELECT a FROM AnswerMsn a WHERE a.endAtMsn > :currentTime AND a.dataType = true  AND (a.totalLandingCnt > 0 OR a.totalPartCnt > 0)")
    public List<AnswerMsn> findByCurrentList(@Param("currentTime") ZonedDateTime currentTime);

    @Query("SELECT a FROM AnswerMsn a WHERE a.dataType = true")
    public List<AnswerMsn> findAllMission();

}
