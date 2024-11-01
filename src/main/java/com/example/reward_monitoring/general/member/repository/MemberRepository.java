package com.example.reward_monitoring.general.member.repository;

import com.example.reward_monitoring.general.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

public interface MemberRepository extends JpaRepository<Member,Integer> {

    public Member findByIdx(int idx);

    public Member findById(String id);


    @Query("SELECT m FROM Member m WHERE m.isActive = :isActive")
    public List<Member> findByIsActive(@Param("isActive") boolean isActive);

    @Query("SELECT m FROM Member m WHERE m.id LIKE %:keyword% ")
    public List<Member> findById_search(@Param("keyword") String keyword);

    @Query("SELECT m FROM Member m WHERE m.name LIKE %:keyword% ")
    public List<Member> findByName_search(@Param("keyword")String keyword);

    @Query("SELECT m FROM Member m WHERE m.createdAt BETWEEN :startDate AND :endDate")
    public List<Member> findByBothDate(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT m FROM Member m WHERE m.createdAt > :startDate")
    public List<Member> findByStartDate(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT m FROM Member m WHERE m.createdAt < :endDate")
    public List<Member> findByEndDate(@Param("endDate") LocalDateTime endDate);


}
