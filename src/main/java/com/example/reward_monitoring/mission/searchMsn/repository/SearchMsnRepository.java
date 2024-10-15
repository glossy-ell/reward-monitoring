package com.example.reward_monitoring.mission.searchMsn.repository;




import com.example.reward_monitoring.mission.searchMsn.entity.SearchMsn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

public interface SearchMsnRepository extends JpaRepository<SearchMsn,Integer> {
    public SearchMsn findByIdx(int idx);

    @Query("SELECT s FROM SearchMsn s WHERE s.startAtMsn > :startDate")
    public List<SearchMsn> findByStartDate(ZonedDateTime startDate);

    @Query("SELECT s FROM SearchMsn s WHERE s.endAtMsn < :endDate")
    public List<SearchMsn> findByEndDate(@Param("endDate") ZonedDateTime endDate);

    @Query("SELECT s FROM SearchMsn s WHERE s.startAtMsn > :startDate AND s.endAtMsn < :endDate")
    public List<SearchMsn> findByBothDate(@Param("startDate") ZonedDateTime startDate, @Param("endDate") ZonedDateTime endDate);

    @Query("SELECT s FROM SearchMsn s WHERE s.startAtCap > :startCap")
    public List<SearchMsn> findByStartAtCap(LocalDate startCap);

    @Query("SELECT s FROM SearchMsn s WHERE s.endAtCap < :endCap")
    public List<SearchMsn> findByEndAtCap(LocalDate endCap);

    @Query("SELECT s FROM SearchMsn s WHERE s.startAtCap > :startCap AND s.endAtCap < :endCap")
    public List<SearchMsn> findByBothCap(@Param("startCap") LocalDate startDate, @Param("endCap") LocalDate endDate);

    @Query("SELECT s FROM SearchMsn s WHERE s.missionActive = : missionActive")
    public List<SearchMsn> findByMissionActive(boolean missionActive);

    @Query("SELECT s FROM SearchMsn s WHERE s.dupParticipation = : dupParticipation")
    public List<SearchMsn> findByDupParticipation(boolean dupParticipation);

    @Query("SELECT s FROM SearchMsn s WHERE s.missionActive = : missionExposure")
    public List<SearchMsn> findByMissionExposure(boolean missionExposure);

    @Query("SELECT s FROM SearchMsn s WHERE s.dataType = :dataType")
    public List<SearchMsn> findByDataType(@Param("dataType") boolean dataType);

    @Query("SELECT s FROM SearchMsn s WHERE s.advertiser.advertiser LIKE %:keyword% ")
    public List<SearchMsn> findByAdvertiser(String keyword);

    @Query("SELECT a FROM AnswerMsn a  WHERE a.advertiserDetails LIKE %:keyword% ")
    public List<SearchMsn> findByAdvertiserDetails(String keyword);

    @Query("SELECT a FROM AnswerMsn a  WHERE a.missionTitle LIKE %:keyword% ")
    public List<SearchMsn> findByMissionTitle(String keyword);
}
