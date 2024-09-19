package com.example.reward_monitoring.statistics.answerMsnStat.sum.Service;


import com.example.reward_monitoring.statistics.answerMsnStat.sum.dto.AnswerMsnSumStatSearchDto;
import com.example.reward_monitoring.statistics.answerMsnStat.sum.entity.AnswerMsnSumStat;
import com.example.reward_monitoring.statistics.answerMsnStat.sum.repository.AnswerMsnSumStatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnswerMsnSumStatService {

    @Autowired
    private AnswerMsnSumStatRepository answerMsnSumStatRepository;

    public List<AnswerMsnSumStat> getAnswerMsnSumStats(){
        return answerMsnSumStatRepository.findAll();
    }

    public List<AnswerMsnSumStat> searchAnswerMsnSum(AnswerMsnSumStatSearchDto dto) {



        List<AnswerMsnSumStat> result = new ArrayList<>();
        if(dto.getUrl() != null)
            result.addAll(answerMsnSumStatRepository.findByServer_ServerUrl(dto.getUrl()));

        if(dto.getAdvertiser()!=null)
            result.addAll(answerMsnSumStatRepository.findByAdvertiser_Advertiser(dto.getAdvertiser()));

        if(dto.getMediacompany()!=null)
            result.addAll(answerMsnSumStatRepository.findByMediaCompany_CompanyName(dto.getMediacompany()));

        if(dto.getStartAt() != null || dto.getEndAt() != null){
            if(dto.getStartAt() != null){
                if(dto.getEndAt() == null)
                    result.addAll(answerMsnSumStatRepository.findByStartAt(dto.getStartAt()));
                else
                    result.addAll(answerMsnSumStatRepository.findByBothAt(dto.getStartAt(),dto.getEndAt()));

            }
            else
                result.addAll(answerMsnSumStatRepository.findByEndAt(dto.getEndAt()));
        }

        return result.stream().distinct().collect(Collectors.toList());
    }
}
