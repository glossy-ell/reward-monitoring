package com.example.reward_monitoring.statistics.answerMsnStat.detail.service;


import com.example.reward_monitoring.statistics.answerMsnStat.daily.entity.AnswerMsnDailyStat;
import com.example.reward_monitoring.statistics.answerMsnStat.detail.dto.AnswerMsnDetailSearchDto;
import com.example.reward_monitoring.statistics.answerMsnStat.detail.entity.AnswerMsnDetailsStat;
import com.example.reward_monitoring.statistics.answerMsnStat.detail.repository.AnswerMsnDetailStatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnswerMsnDetailService {

    @Autowired
    private AnswerMsnDetailStatRepository answerMsnDetailStatRepository;

    public List<AnswerMsnDetailsStat> getAnswerMsnsDetails() {
        return answerMsnDetailStatRepository.findAll();
    }

    public List<AnswerMsnDetailsStat> searchAnswerMsnDetail(AnswerMsnDetailSearchDto dto) {

        List<AnswerMsnDetailsStat> target_idx;
        List<AnswerMsnDetailsStat> result = new ArrayList<>();

        if(dto.getUrl()!=null){
            result.addAll(answerMsnDetailStatRepository.findByServer_ServerUrl(dto.getUrl()));
        }
        if(dto.getStartAt() != null || dto.getEndAt() != null)
            if(dto.getStartAt() != null) {
                if (dto.getEndAt() == null)
                    result.addAll(answerMsnDetailStatRepository.findByStartAt(dto.getStartAt()));
                else
                    result.addAll(answerMsnDetailStatRepository.findByBothAt(dto.getStartAt(), dto.getEndAt()));
            }
            else
                result.addAll(answerMsnDetailStatRepository.findByEndAt(dto.getEndAt()));
        if(dto.getMediacompany()!=null)
            result.addAll(answerMsnDetailStatRepository.findByMediaCompany_companyName(dto.getMediacompany()));
        if(dto.getIsAbuse()!=null)
            result.addAll(answerMsnDetailStatRepository.findByIsAbuse(dto.getIsAbuse()));
        if(dto.getResponse()!=null)
            result.addAll(answerMsnDetailStatRepository.findByResponse(dto.getResponse()));
        if(dto.getAdvertiser()!=null)
            result.addAll(answerMsnDetailStatRepository.findByAdvertiser_Advertiser(dto.getAdvertiser()));
        if(dto.getIdx()!=null)
            result.addAll(answerMsnDetailStatRepository.findByAnswerMsn_Idx(dto.getIdx()));


        return result.stream().distinct().collect(Collectors.toList());
    }
}
