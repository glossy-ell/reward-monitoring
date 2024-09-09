package com.example.reward_monitoring.mission.answerMsn.service;

import com.example.reward_monitoring.mission.answerMsn.dto.AnswerMsnEditDto;
import com.example.reward_monitoring.mission.answerMsn.dto.AnswerMsnReadDto;
import com.example.reward_monitoring.mission.answerMsn.entity.AnswerMsn;
import com.example.reward_monitoring.mission.answerMsn.repository.AnswerMsnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnswerMsnService {

    @Autowired
    private AnswerMsnRepository answerMsnRepository;

    public AnswerMsn edit(int idx, AnswerMsnEditDto dto) {
        AnswerMsn answerMsn =answerMsnRepository.findByIdx(idx);
        if(answerMsn==null)
            return null;
        if(dto.getMissionDefaultQty() != null)
            answerMsn.setMissionDefaultQty(dto.getMissionDefaultQty());
        if(dto.getMissionDailyCap() !=null)
            answerMsn.setMissionDailyCap(dto.getMissionDailyCap());
        if(dto.getMissionTitle()!=null)
            answerMsn.setMissionTitle(dto.getMissionTitle());
        if(dto.getMissionAnswer()!=null)
            answerMsn.setMissionAnswer(dto.getMissionAnswer());
        if (dto.getStartAtMsn() != null)
            answerMsn.setStartAtMsn(dto.getStartAtMsn());
        if (dto.getEndAtMsn() != null)
            answerMsn.setEndAtMsn(dto.getEndAtMsn());
        if (dto.getStartAtCap() != null)
            answerMsn.setStartAtCap(dto.getStartAtCap());
        if (dto.getEndAtCap() != null)
            answerMsn.setEndAtCap(dto.getEndAtCap());
        if (dto.getMissionActive() != null) {
            boolean bool = dto.getMissionActive();
            answerMsn.setMissionActive(bool);
        }
        if (dto.getMissionExposure() != null) {
            boolean bool = dto.getMissionExposure();
            answerMsn.setMissionExposure(bool);
        }
        if (dto.getDupParticipation() != null) {
            boolean bool = dto.getDupParticipation();
            answerMsn.setDupParticipation(bool);
        }
        if (dto.getReEngagementDay() != null) {
            answerMsn.setReEngagementDay(dto.getReEngagementDay());
        }
        return answerMsn;
    }

    public AnswerMsn add(AnswerMsnReadDto dto) {
        return dto.toEntity();
    }

    public AnswerMsn getAnswerMsn(int idx) {
        return answerMsnRepository.findByIdx(idx);
    }

    public List<AnswerMsn> getAnswerMsns() {
        return answerMsnRepository.findAll();
    }

    public AnswerMsn delete(int idx) {
        AnswerMsn target = answerMsnRepository.findByIdx(idx);
        if(target==null)
            return null;
        answerMsnRepository.delete(target);
        return target;
    }
}
