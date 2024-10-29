package com.example.reward_monitoring.statistics.searchMsn.sum.repository;





import com.example.reward_monitoring.statistics.searchMsn.sum.entity.SearchMsnSumStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.time.LocalDate;
import java.util.List;

public interface SearchMsnSumStatRepository extends JpaRepository<SearchMsnSumStat,Integer> {

    @Query("SELECT s FROM SearchMsnSumStat s WHERE s.date > :startAt")
    public List<SearchMsnSumStat> findByStartAt(@Param("startAt") LocalDate startAt);

    @Query("SELECT s FROM SearchMsnSumStat s WHERE s.date < :endAt")
    public List<SearchMsnSumStat> findByEndAt(@Param("endAt") LocalDate endAt);

    @Query("SELECT s FROM SearchMsnSumStat s WHERE s.date BETWEEN :startAt AND :endAt")
    public List<SearchMsnSumStat>  findByBothAt(@Param("startAt") LocalDate startAt, @Param("endAt") LocalDate endAt);


    public List<SearchMsnSumStat>  findByAdvertiser_Advertiser(String advertiser);

    public List<SearchMsnSumStat>  findByServer_ServerUrl(String url);

    public List<SearchMsnSumStat>  findByMediaCompany_CompanyName(String companyName);

    @Query("SELECT s FROM SearchMsnSumStat s WHERE s.date BETWEEN :past AND :currentTime")
    public List<SearchMsnSumStat> findMonth(@Param("currentTime") LocalDate currentTime, @Param("past") LocalDate past);
}
