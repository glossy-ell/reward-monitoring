package com.example.reward_monitoring.statistics.searchMsn.detail.repository;



import com.example.reward_monitoring.statistics.searchMsn.detail.entity.SearchMsnDetailsStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SearchMsnDetailStatRepository extends JpaRepository<SearchMsnDetailsStat,Integer> {

    @Query("SELECT s FROM SaveMsnDetailsStat s WHERE s.registrationDate > :startAt")
    public List<SearchMsnDetailsStat> findByStartAt(LocalDate startAt);

    @Query("SELECT s FROM SaveMsnDetailsStat s WHERE s.registrationDate < :endAt")
    public List<SearchMsnDetailsStat> findByEndAt(LocalDate endAt);

    @Query("SELECT s FROM SaveMsnDetailsStat s WHERE s.registrationDate BETWEEN :startAt AND :endAt")
    public List<SearchMsnDetailsStat> findByBothAt(@Param("startAt") LocalDate startAt, @Param("endAt") LocalDate endAt);

    public List<SearchMsnDetailsStat> findByAdvertiser_Advertiser(String advertiser);

    public List<SearchMsnDetailsStat> findByServer_ServerUrl(String url);

    public List<SearchMsnDetailsStat> findByMediaCompany_companyName(String companyName);

    public List<SearchMsnDetailsStat> findByIsAbuse(boolean check);

    public List<SearchMsnDetailsStat> findByResponse(boolean check);

    public List<SearchMsnDetailsStat> findBySearchMsn_Idx(int idx);
}
