package com.example.reward_monitoring.mission.searchMsn.repository;



import com.example.reward_monitoring.mission.searchMsn.entity.SearchMsn;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchMsnRepositroy extends JpaRepository<SearchMsn,Integer> {
    public SearchMsn findByIdx(int idx);
}
