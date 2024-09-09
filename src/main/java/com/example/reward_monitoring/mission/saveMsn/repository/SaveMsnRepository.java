package com.example.reward_monitoring.mission.saveMsn.repository;

import com.example.reward_monitoring.mission.saveMsn.entity.SaveMsn;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaveMsnRepository extends JpaRepository<SaveMsn,Integer> {
    public SaveMsn findByIdx(int idx);
}
