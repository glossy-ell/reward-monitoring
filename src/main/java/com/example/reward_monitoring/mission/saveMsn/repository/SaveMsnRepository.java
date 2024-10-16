package com.example.reward_monitoring.mission.saveMsn.repository;

import com.example.reward_monitoring.mission.answerMsn.entity.AnswerMsn;
import com.example.reward_monitoring.mission.saveMsn.entity.SaveMsn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

public interface SaveMsnRepository extends JpaRepository<SaveMsn,Integer> {
    public SaveMsn findByIdx(int idx);


    @Query("SELECT s FROM SaveMsn s WHERE s.startAtMsn > :startDate")
    public List<SaveMsn> findByStartDate(ZonedDateTime startDate);

    @Query("SELECT s FROM SaveMsn s WHERE s.endAtMsn < :endDate")
    public List<SaveMsn> findByEndDate(@Param("endDate") ZonedDateTime endDate);

    @Query("SELECT s FROM SaveMsn s WHERE s.startAtMsn > :startDate AND s.endAtMsn < :endDate")
    public List<SaveMsn> findByBothDate(@Param("startDate") ZonedDateTime startDate, @Param("endDate") ZonedDateTime endDate);

    @Query("SELECT s FROM SaveMsn s WHERE s.startAtCap > :startCap")
    public List<SaveMsn> findByStartAtCap(LocalDate startCap);

    @Query("SELECT s FROM SaveMsn s WHERE s.endAtCap < :endCap")
    public List<SaveMsn> findByEndAtCap(LocalDate endCap);

    @Query("SELECT s FROM SaveMsn s WHERE s.startAtCap > :startCap AND s.endAtCap < :endCap")
    public List<SaveMsn> findByBothCap(@Param("startCap") LocalDate startCap, @Param("endCap") LocalDate endCap);

    @Query("SELECT s FROM SaveMsn s WHERE s.missionActive = : missionActive")
    public List<SaveMsn> findByMissionActive(boolean missionActive);

    @Query("SELECT s FROM SaveMsn s WHERE s.dupParticipation = : dupParticipation")
    public List<SaveMsn> findByDupParticipation(boolean dupParticipation);

    @Query("SELECT s FROM SaveMsn s WHERE s.missionExposure = : missionExposure")
    public List<SaveMsn> findByMissionExposure(boolean missionExposure);

    @Query("SELECT s FROM SaveMsn s WHERE s.dataType = : dataType")
    public List<SaveMsn> findByDataType(boolean dataType);

    @Query("SELECT s FROM SaveMsn s  WHERE s.advertiser.advertiser LIKE %:keyword% ")
    public List<SaveMsn> findByAdvertiser(String keyword);

    @Query("SELECT s FROM SaveMsn s  WHERE s.advertiserDetails LIKE %:keyword% ")
    public List<SaveMsn> findByAdvertiserDetails(String keyword);

    @Query("SELECT s FROM SaveMsn s  WHERE s.missionTitle LIKE %:keyword% ")
    public List<SaveMsn> findByMissionTitle(String keyword);

    @Query("SELECT s FROM SaveMsn s WHERE s.server.serverUrl LIKE %:keyword%")
    public List<AnswerMsn> findByServer(@Param("keyword") String keyword);
}
