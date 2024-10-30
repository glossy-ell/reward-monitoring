package com.example.reward_monitoring.mission.missionCS.repository;




import com.example.reward_monitoring.mission.missionCS.entity.MissionCS;
import com.example.reward_monitoring.mission.missionCS.model.CSType;
import com.example.reward_monitoring.mission.missionCS.model.MsnType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface MissionCSRepository extends JpaRepository<MissionCS,Integer> {
    public MissionCS findByIdx(int idx);

    @Query("SELECT m FROM MissionCS m WHERE m.firstRegDate BETWEEN :startDate AND :endDate")
    public List<MissionCS> findByBothDate(@Param("startDate") LocalDateTime startDate, @Param("endDate")LocalDateTime endDate);

    @Query("SELECT m FROM MissionCS m WHERE m.firstRegDate > :startAt")
    public List<MissionCS> findByStartAt(@Param("startAt")LocalDateTime startAt);

    @Query("SELECT m FROM MissionCS m WHERE m.firstRegDate < :endAt")
    public List<MissionCS> findByEndAt(@Param("endAt")LocalDateTime endAt);

    public List<MissionCS> findByCsType(CSType type);

    public List<MissionCS> findByMsnType(MsnType msnType);

    @Query("SELECT m FROM MissionCS m  WHERE m.msnTitle LIKE %:keyword% ")
    public List<MissionCS> findByMissionTitle(@Param("keyword") String keyword);
}
