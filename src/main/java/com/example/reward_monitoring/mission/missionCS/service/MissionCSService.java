package com.example.reward_monitoring.mission.missionCS.service;


import com.example.reward_monitoring.mission.answerMsn.entity.AnswerMsn;
import com.example.reward_monitoring.mission.missionCS.dto.MissionCSSearchDto;
import com.example.reward_monitoring.mission.missionCS.entity.MissionCS;
import com.example.reward_monitoring.mission.missionCS.repository.MissionCSRepository;
import com.example.reward_monitoring.mission.saveMsn.entity.SaveMsn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<MissionCS> searchSMissionCS(MissionCSSearchDto dto) {
        List<MissionCS> target_cs_type;
        List<MissionCS> target_msn_type;
        List<MissionCS> target_date;
        List<MissionCS> target_msn_title;
        List<MissionCS> result = new ArrayList<>();

        if(dto.getCsType() != null){
            target_cs_type = missionCSRepository.findByCsType(String.valueOf(dto.getCsType()));
            result.addAll(target_cs_type);
        }
        if(dto.getMsnType() != null){
            target_msn_type = missionCSRepository.findByMissionTitle(String.valueOf(dto.getCsType()));
            result.addAll(target_msn_type);
        }

        if(dto.getStartAtMsn() != null || dto.getEndAtMsn() != null){
            if(dto.getStartAtMsn() != null){
                ZoneId zoneId = ZoneId.of("Asia/Seoul");
                ZonedDateTime start_time = dto.getStartAtMsn().atStartOfDay(zoneId);
                if(dto.getEndAtMsn() == null){
                    target_date = missionCSRepository.findByStartAt(start_time);
                    result.addAll(target_date);
                }else{
                    ZonedDateTime end_time = dto.getEndAtMsn().atStartOfDay(zoneId);
                    target_date = missionCSRepository.findByBothDate(start_time,end_time);
                    result.addAll(target_date);
                }

            }
            else {
                ZoneId zoneId = ZoneId.of("Asia/Seoul");
                ZonedDateTime end_time = dto.getEndAtMsn().atStartOfDay(zoneId);

                target_date = missionCSRepository.findByEndAt(end_time);
                result.addAll(target_date);
            }

        }
        if(dto.getMsnTitle() != null){
            target_msn_title = missionCSRepository.findByMissionTitle(dto.getMsnTitle());
            result.addAll(target_msn_title);
        }

        return result.stream().distinct().collect(Collectors.toList());
   }
}
