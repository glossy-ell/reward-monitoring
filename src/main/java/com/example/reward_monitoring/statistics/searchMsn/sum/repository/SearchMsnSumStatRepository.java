package com.example.reward_monitoring.statistics.searchMsn.sum.repository;




<<<<<<< Updated upstream
import com.example.reward_monitoring.statistics.answerMsnStat.sum.entity.AnswerMsnSumStat;
=======

>>>>>>> Stashed changes
import com.example.reward_monitoring.statistics.searchMsn.sum.entity.SearchMsnSumStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SearchMsnSumStatRepository extends JpaRepository<SearchMsnSumStat,Integer> {

    @Query("SELECT s FROM SearchMsnSumStat s WHERE s.date > :startAt")
    public List<SearchMsnSumStat> findByStartAt(LocalDate startAt);

    @Query("SELECT s FROM SearchMsnSumStat s WHERE s.date < :endAt")
    public List<SearchMsnSumStat> findByEndAt(LocalDate endAt);

    @Query("SELECT s FROM SearchMsnSumStat s WHERE s.date BETWEEN :startAt AND :endAt")
    public List<SearchMsnSumStat>  findByBothAt(@Param("startAt") LocalDate startAt, @Param("endAt") LocalDate endAt);


    public List<SearchMsnSumStat>  findByAdvertiser_Advertiser(String advertiser);

    public List<SearchMsnSumStat>  findByServer_ServerUrl(String url);

    public List<SearchMsnSumStat>  findByMediaCompany_CompanyName(String companyName);

<<<<<<< Updated upstream
    @Query("SELECT a FROM AnswerMsnSumStat a WHERE a.date BETWEEN :past AND :currentTime")
=======
    @Query("SELECT s FROM SearchMsnSumStat s WHERE s.date BETWEEN :past AND :currentTime")
>>>>>>> Stashed changes
    public List<SearchMsnSumStat> findMonth(LocalDate currentTime, LocalDate past);
}
