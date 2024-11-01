package com.example.reward_monitoring.mission.searchMsn.repository;



import com.example.reward_monitoring.mission.searchMsn.entity.SearchMsn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface SearchMsnRepository extends JpaRepository<SearchMsn,Integer> {
    public SearchMsn findByIdx(int idx);

    @Query("SELECT s FROM SearchMsn s WHERE s.startAtMsn > :startDate")
    public List<SearchMsn> findByStartDate(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT s FROM SearchMsn s WHERE s.endAtMsn < :endDate")
    public List<SearchMsn> findByEndDate(@Param("endDate") LocalDateTime endDate);

    @Query("SELECT s FROM SearchMsn s WHERE s.startAtMsn > :startDate AND s.endAtMsn < :endDate")
    public List<SearchMsn> findByBothDate(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT s FROM SearchMsn s WHERE s.startAtCap > :startCap")
    public List<SearchMsn> findByStartAtCap(@Param("startCap") LocalDate startCap);

    @Query("SELECT s FROM SearchMsn s WHERE s.endAtCap < :endCap")
    public List<SearchMsn> findByEndAtCap(@Param("endCap") LocalDate endCap);

    @Query("SELECT s FROM SearchMsn s WHERE s.startAtCap > :startCap AND s.endAtCap < :endCap")
    public List<SearchMsn> findByBothCap(@Param("startCap") LocalDate startDate, @Param("endCap") LocalDate endDate);

    @Query("SELECT s FROM SearchMsn s WHERE s.missionActive = :missionActive")
    public List<SearchMsn> findByMissionActive(@Param("missionActive") boolean missionActive);

    @Query("SELECT s FROM SearchMsn s WHERE s.dupParticipation = :dupParticipation")
    public List<SearchMsn> findByDupParticipation(@Param("dupParticipation") boolean dupParticipation);

    @Query("SELECT s FROM SearchMsn s WHERE s.missionExposure = :missionExposure")
    public List<SearchMsn> findByMissionExposure(@Param("missionExposure") boolean missionExposure);

    @Query("SELECT s FROM SearchMsn s WHERE s.dataType = :dataType")
    public List<SearchMsn> findByDataType(@Param("dataType") boolean dataType);

    @Query("SELECT s FROM SearchMsn s WHERE s.advertiser.advertiser LIKE %:keyword% ")
    public List<SearchMsn> findByAdvertiser(@Param("keyword") String keyword);

    @Query("SELECT s FROM SearchMsn s  WHERE s.advertiserDetails LIKE %:keyword% ")
    public List<SearchMsn> findByAdvertiserDetails(@Param("keyword")String keyword);

    @Query("SELECT s FROM SearchMsn s  WHERE s.missionTitle LIKE %:keyword% ")
    public List<SearchMsn> findByMissionTitle(@Param("keyword")String keyword);

    @Query("SELECT s FROM SearchMsn s WHERE s.server.serverUrl LIKE %:keyword%")
    public List<SearchMsn> findByServer_(@Param("keyword") String keyword);

    @Query("SELECT s FROM SearchMsn s WHERE s.endAtMsn > :currentTime AND s.dataType = true AND (s.totalLandingCnt > 0 OR s.totalPartCnt > 0)")
    public List<SearchMsn> findByCurrentList(@Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT s FROM SearchMsn s WHERE s.dataType = true")
    public List<SearchMsn> findAllMission();

    @Query("SELECT s FROM SearchMsn s WHERE (s.endAtMsn >= :currentTime) AND (s.dataType = true)  AND (s.totalLandingCnt > 0 OR s.totalPartCnt > 0) AND (s.mediaCompany.idx = :aidx) ")
    public List<SearchMsn> findByCurrentListAffiliate(@Param("currentTime") LocalDateTime currentTime, @Param("aidx")int aidx);
}
