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
import java.util.Set;
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
        List<MissionCS> target_cs_type=null;
        List<MissionCS> target_msn_type=null;
        List<MissionCS> target_date=null;
        List<MissionCS> target_msn_title=null;
        List<MissionCS> result;
        boolean changed = false;

        if(dto.getCsType() != null){
            if(dto.getCsType().equals("A"))
                target_cs_type = missionCSRepository.findByCsType("정답제목확인");
            else if(dto.getCsType().equals("U"))
                target_cs_type = missionCSRepository.findByCsType("URL확인");
            else if(dto.getCsType().equals("D"))
                target_cs_type = missionCSRepository.findByCsType("데이터유실");
        }
        if(dto.getMsnType() != null){
            if(dto.getMsnType().equals("Q"))
                target_cs_type = missionCSRepository.findByCsType("정답");
            else if(dto.getMsnType().equals("R"))
                target_cs_type = missionCSRepository.findByCsType("검색");
            else if(dto.getMsnType().equals("S"))
                target_cs_type = missionCSRepository.findByCsType("저장");
        }


        if(dto.getStartAtMsn() != null || dto.getEndAtMsn() != null){
            if(dto.getStartAtMsn() != null){
                if(dto.getEndAtMsn() == null){
                    target_date = missionCSRepository.findByStartAt(dto.getStartAtMsn().atStartOfDay());
                }else{
                    target_date = missionCSRepository.findByBothDate(dto.getStartAtMsn().atStartOfDay(),dto.getEndAtMsn().atStartOfDay().plusHours(23).plusMinutes(59));
                }

            }
            else {

                target_date = missionCSRepository.findByEndAt(dto.getEndAtMsn().atStartOfDay().plusHours(23).plusMinutes(59));
            }

        }
        if(dto.getMsnType() != null){
            target_msn_type = missionCSRepository.findByMissionTitle(dto.getMsnType());
        }
        if(dto.getMsnTitle() != null){
            target_msn_title = missionCSRepository.findByMissionTitle(dto.getMsnTitle());
        }

        result = new ArrayList<>(missionCSRepository.findAll());

        if(target_cs_type != null){
            Set<Integer> idxSet = target_cs_type.stream().map(MissionCS::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(missionCS -> idxSet.contains(missionCS.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }

        if(target_msn_type != null){
            Set<Integer> idxSet = target_msn_type.stream().map(MissionCS::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(missionCS -> idxSet.contains(missionCS.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }

        if(target_date != null){
            Set<Integer> idxSet = target_date.stream().map(MissionCS::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(missionCS -> idxSet.contains(missionCS.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }

        if(target_msn_title != null){
            Set<Integer> idxSet = target_msn_title.stream().map(MissionCS::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(missionCS -> idxSet.contains(missionCS.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }



        if(!changed)
            result = new ArrayList<>();

        return result;
   }
}
