package com.example.reward_monitoring.mission.missionCS.repository;



import com.example.reward_monitoring.mission.missionCS.entity.MissionCS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.List;

public interface MissionCSRepository extends JpaRepository<MissionCS,Integer> {
    public MissionCS findByIdx(int idx);

    //@Query("SELECT * FROM member m WHERE m.createdAt BETWEEN :startDate AND :endDate")
    //public List<Member> findBySearch(@Param("startDate") ZonedDateTime startDate, @Param("endDate") ZonedDateTime endDate);
}
