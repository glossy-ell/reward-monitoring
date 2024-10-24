package com.example.reward_monitoring.statistics.answerMsnStat.detail.service;


import com.example.reward_monitoring.statistics.answerMsnStat.detail.dto.AnswerMsnDetailSearchDto;
import com.example.reward_monitoring.statistics.answerMsnStat.detail.entity.AnswerMsnDetailsStat;
import com.example.reward_monitoring.statistics.answerMsnStat.detail.repository.AnswerMsnDetailStatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnswerMsnDetailService {

    @Autowired
    private AnswerMsnDetailStatRepository answerMsnDetailStatRepository;

    public List<AnswerMsnDetailsStat> getAnswerMsnsDetails() {
        return answerMsnDetailStatRepository.findAll();
    }


    public List<AnswerMsnDetailsStat> searchAnswerMsnDetail(AnswerMsnDetailSearchDto dto) {

        List<AnswerMsnDetailsStat> target_date = null;
        List<AnswerMsnDetailsStat> target_serverUrl = null;
        List<AnswerMsnDetailsStat> target_mediaCompany = null;


        List<AnswerMsnDetailsStat> target_isAbuse = null;
        List<AnswerMsnDetailsStat> target_response = null;
        List<AnswerMsnDetailsStat> target_advertiser = null;
        List<AnswerMsnDetailsStat> target_idx = null;
        List<AnswerMsnDetailsStat> result;
        boolean changed = false;

        if(dto.getUrl()!=null){
            target_serverUrl = answerMsnDetailStatRepository.findByServer_ServerUrl(dto.getUrl());
        }
        if(dto.getStartAt() != null || dto.getEndAt() != null)
            if(dto.getStartAt() != null) {
                if (dto.getEndAt() == null)
                    target_date = answerMsnDetailStatRepository.findByStartAt(dto.getStartAt());
                else
                    target_date = answerMsnDetailStatRepository.findByBothAt(dto.getStartAt(), dto.getEndAt());
            }
            else
                target_date = answerMsnDetailStatRepository.findByEndAt(dto.getEndAt());

        if(dto.getMediacompany()!=null)
            target_mediaCompany = answerMsnDetailStatRepository.findByMediaCompany_companyName(dto.getMediacompany());
        if(dto.getIsAbuse()!=null)
            target_isAbuse = answerMsnDetailStatRepository.findByIsAbuse(dto.getIsAbuse());
        if(dto.getResponse()!=null)
            target_response = answerMsnDetailStatRepository.findByResponse(dto.getResponse());

        if(dto.getAdvertiser()!=null)
            target_advertiser = answerMsnDetailStatRepository.findByAdvertiser_Advertiser(dto.getAdvertiser());
        if(dto.getIdx()!=null)
            target_idx = answerMsnDetailStatRepository.findByAnswerMsn_Idx(dto.getIdx());


        result = new ArrayList<>(answerMsnDetailStatRepository.findAll());

        if(target_date!= null) {
            Set<Integer> idxSet = target_date.stream().map(AnswerMsnDetailsStat::getTX).collect(Collectors.toSet());
            result = result.stream().filter(answerMsnDetailsStat -> idxSet.contains(answerMsnDetailsStat.getTX())).distinct().collect(Collectors.toList());
            changed = true;
        }
        if(target_serverUrl!= null) {
            Set<Integer> idxSet = target_serverUrl.stream().map(AnswerMsnDetailsStat::getTX).collect(Collectors.toSet());
            result = result.stream().filter(answerMsnDetailsStat -> idxSet.contains(answerMsnDetailsStat.getTX())).distinct().collect(Collectors.toList());
            changed = true;
        }
        if(target_mediaCompany!= null) {
            Set<Integer> idxSet = target_mediaCompany.stream().map(AnswerMsnDetailsStat::getTX).collect(Collectors.toSet());
            result = result.stream().filter(answerMsnDetailsStat -> idxSet.contains(answerMsnDetailsStat.getTX())).distinct().collect(Collectors.toList());
            changed = true;
        }

        if(target_mediaCompany!= null) {
            Set<Integer> idxSet = target_mediaCompany.stream().map(AnswerMsnDetailsStat::getTX).collect(Collectors.toSet());
            result = result.stream().filter(answerMsnDetailsStat -> idxSet.contains(answerMsnDetailsStat.getTX())).distinct().collect(Collectors.toList());
            changed = true;
        }


        if(target_isAbuse!= null) {
            Set<Integer> idxSet = target_isAbuse.stream().map(AnswerMsnDetailsStat::getTX).collect(Collectors.toSet());
            result = result.stream().filter(answerMsnDetailsStat -> idxSet.contains(answerMsnDetailsStat.getTX())).distinct().collect(Collectors.toList());
            changed = true;
        }

        if(target_response!= null) {
            Set<Integer> idxSet = target_response.stream().map(AnswerMsnDetailsStat::getTX).collect(Collectors.toSet());
            result = result.stream().filter(answerMsnDetailsStat -> idxSet.contains(answerMsnDetailsStat.getTX())).distinct().collect(Collectors.toList());
            changed = true;
        }

        if(target_advertiser != null) {
            Set<Integer> idxSet = target_advertiser.stream().map(AnswerMsnDetailsStat::getTX).collect(Collectors.toSet());
            result = result.stream().filter(answerMsnDetailsStat -> idxSet.contains(answerMsnDetailsStat.getTX())).distinct().collect(Collectors.toList());
            changed = true;
        }
        if(target_idx != null) {
            Set<Integer> idxSet = target_idx.stream().map(AnswerMsnDetailsStat::getTX).collect(Collectors.toSet());
            result = result.stream().filter(answerMsnDetailsStat -> idxSet.contains(answerMsnDetailsStat.getTX())).distinct().collect(Collectors.toList());
            changed = true;
        }


        if(!changed)
            result = new ArrayList<>();

        if(dto.getSOrder().equals("memberId")){
            Map<Integer, AnswerMsnDetailsStat> groupedResult = result.stream().collect(Collectors.toMap(
                    AnswerMsnDetailsStat::getTX,
                    stat -> stat, // 값은 AnswerMsnDetailsStat 객체
                    (existing, replacement) -> {
                        // 날짜 비교하여 최신 것 선택
                        return existing.getRegistrationDate().isAfter(replacement.getRegistrationDate()) ? existing : replacement;
                    }
            ));
            result  = groupedResult.values().stream().sorted(Comparator.comparing(AnswerMsnDetailsStat::getRegistrationDate).reversed()).collect(Collectors.toList());
        }

        return result;
    }
}
