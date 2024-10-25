package com.example.reward_monitoring.general.mediaCompany.repository;

import com.example.reward_monitoring.general.advertiser.entity.Advertiser;
import com.example.reward_monitoring.general.mediaCompany.entity.MediaCompany;
import com.example.reward_monitoring.general.mediaCompany.model.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.List;

public interface MediaCompanyRepository extends JpaRepository<MediaCompany,Integer> {

    public MediaCompany findByIdx(int idx);

    @Query("SELECT m FROM MediaCompany m WHERE m.isActive = :isActive")
    public List<MediaCompany> findByIsActive(boolean isActive);

    @Query("SELECT m FROM MediaCompany m WHERE m.companyName LIKE %:keyword% ")
    public List<MediaCompany> findByName(@Param("keyword") String keyword);

    @Query("SELECT m FROM MediaCompany m WHERE m.APIKey LIKE %:keyword% ")
    public List<MediaCompany> findByApi(@Param("keyword")String keyword);

    @Query("SELECT m FROM MediaCompany m WHERE m.createdAt BETWEEN :startDate AND :endDate")
    public List<MediaCompany> findByBothDate(@Param("startDate") ZonedDateTime startDate, @Param("endDate") ZonedDateTime endDate);

    @Query("SELECT m FROM MediaCompany m WHERE m.createdAt > :startDate")
    public List<MediaCompany> findByStartDate(@Param("startDate") ZonedDateTime startDate);

    @Query("SELECT m FROM MediaCompany m WHERE m.createdAt < :endDate")
    public List<MediaCompany> findByEndDate(@Param("endDate") ZonedDateTime endDate);

    @Query("SELECT m FROM MediaCompany m WHERE m.type = :type")
    public List<MediaCompany> findByOperationType(@Param("type") Type type);

}
