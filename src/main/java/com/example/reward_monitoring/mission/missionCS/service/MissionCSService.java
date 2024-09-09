package com.example.reward_monitoring.mission.missionCS.service;


import com.example.reward_monitoring.mission.missionCS.entity.MissionCS;
import com.example.reward_monitoring.mission.missionCS.repository.MissionCSRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MissionCSService {

    @Autowired
    private MissionCSRepository missionCSRepository;


    public MissionCS getMissionCS(int idx) {
        return missionCSRepository.findByIdx(idx);
    }

    public List<MissionCS> getMissionCSs() {
        return missionCSRepository.findAll();
    }

    public MissionCS delete(int idx) {
        MissionCS target = missionCSRepository.findByIdx(idx);
        if(target == null)
            return null;
        missionCSRepository.delete(target);
        return target;
    }
}
