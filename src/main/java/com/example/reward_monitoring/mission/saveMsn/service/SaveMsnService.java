package com.example.reward_monitoring.mission.saveMsn.service;



import com.example.reward_monitoring.mission.saveMsn.dto.SaveMsnEditDto;
import com.example.reward_monitoring.mission.saveMsn.dto.SaveMsnReadDto;
import com.example.reward_monitoring.mission.saveMsn.entity.SaveMsn;
import com.example.reward_monitoring.mission.saveMsn.repository.SaveMsnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Service
public class SaveMsnService {
    @Autowired
    private SaveMsnRepository saveMsnRepository;

    public SaveMsn edit(int idx, SaveMsnEditDto dto) {
        SaveMsn saveMsn =saveMsnRepository.findByIdx(idx);
        if(saveMsn==null)
            return null;
        if(dto.getMissionDefaultQty() != null)
            saveMsn.setMissionDefaultQty(dto.getMissionDefaultQty());
        if(dto.getMissionDailyCap() !=null)
            saveMsn.setMissionDailyCap(dto.getMissionDailyCap());
        if(dto.getMissionTitle()!=null)
            saveMsn.setMissionTitle(dto.getMissionTitle());
        if(dto.getSearchKeyword()!=null)
            saveMsn.setSearchKeyword(dto.getSearchKeyword());
        if (dto.getStartAtMsn() != null)
            saveMsn.setStartAtMsn(dto.getStartAtMsn());
        if (dto.getEndAtMsn() != null)
            saveMsn.setEndAtMsn(dto.getEndAtMsn());
        if (dto.getStartAtCap() != null)
            saveMsn.setStartAtCap(dto.getStartAtCap());
        if (dto.getEndAtCap() != null)
            saveMsn.setEndAtCap(dto.getEndAtCap());
        if (dto.getMissionActive() != null) {
            boolean bool = dto.getMissionActive();
            saveMsn.setMissionActive(bool);
        }
        if (dto.getMissionExposure() != null) {
            boolean bool = dto.getMissionExposure();
            saveMsn.setMissionExposure(bool);
        }
        if (dto.getDupParticipation() != null) {
            boolean bool = dto.getDupParticipation();
            saveMsn.setDupParticipation(bool);
        }
        if (dto.getReEngagementDay() != null) {
            saveMsn.setReEngagementDay(dto.getReEngagementDay());
        }
        return saveMsn;
    }

    public SaveMsn add(SaveMsnReadDto dto) {
        return dto.toEntity();
    }

    public SaveMsn getSaveMsn(int idx) {
        return saveMsnRepository.findByIdx(idx);
    }

    public List<SaveMsn> getSaveMsns() {
        return saveMsnRepository.findAll();
    }

    public SaveMsn delete(int idx) {
        SaveMsn target = saveMsnRepository.findByIdx(idx);
        if(target==null)
            return null;
        saveMsnRepository.delete(target);
        return target;
    }
}
