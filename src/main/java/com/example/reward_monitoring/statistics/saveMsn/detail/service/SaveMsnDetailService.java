package com.example.reward_monitoring.statistics.saveMsn.detail.service;


import com.example.reward_monitoring.statistics.saveMsn.detail.dto.SaveMsnDetailSearchDto;
import com.example.reward_monitoring.statistics.saveMsn.detail.entity.SaveMsnDetailsStat;
import com.example.reward_monitoring.statistics.saveMsn.detail.repository.SaveMsnDetailStatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SaveMsnDetailService {

    @Autowired
    private SaveMsnDetailStatRepository saveMsnDetailStatRepository;

    public List<SaveMsnDetailsStat> getSaveMsnsDetails() {
        return saveMsnDetailStatRepository.findAll();
    }

    public List<SaveMsnDetailsStat> searchSaveMsnDetail(SaveMsnDetailSearchDto dto) {

        List<SaveMsnDetailsStat> target_date = null;
        List<SaveMsnDetailsStat> target_serverUrl = null;
        List<SaveMsnDetailsStat> target_mediaCompany = null;


        List<SaveMsnDetailsStat> target_isAbuse = null;
        List<SaveMsnDetailsStat> target_response = null;
        List<SaveMsnDetailsStat> target_advertiser = null;
        List<SaveMsnDetailsStat> target_idx = null;
        List<SaveMsnDetailsStat> result;
        boolean changed = false;

        if(dto.getUrl()!=null){
            target_serverUrl = saveMsnDetailStatRepository.findByServer_ServerUrl(dto.getUrl());
        }
        if(dto.getStartAt() != null || dto.getEndAt() != null)
            if(dto.getStartAt() != null) {
                if (dto.getEndAt() == null)
                    target_date = saveMsnDetailStatRepository.findByStartAt(dto.getStartAt().atStartOfDay());
                else
                    target_date = saveMsnDetailStatRepository.findByBothAt(dto.getStartAt().atStartOfDay(), dto.getEndAt().atStartOfDay().plusHours(23).plusMinutes(59));
            }
            else
                target_date = saveMsnDetailStatRepository.findByEndAt(dto.getEndAt().atStartOfDay().plusHours(23).plusMinutes(59));

        if(dto.getMediacompany()!=null)
            target_mediaCompany = saveMsnDetailStatRepository.findByMediaCompany_companyName(dto.getMediacompany());
        if(dto.getIsAbuse()!=null)
            target_isAbuse = saveMsnDetailStatRepository.findByIsAbuse(dto.getIsAbuse());
        if(dto.getResponse()!=null)
            target_response = saveMsnDetailStatRepository.findByResponse(dto.getResponse());

        if(dto.getAdvertiser()!=null)
            target_advertiser = saveMsnDetailStatRepository.findByAdvertiser_Advertiser(dto.getAdvertiser());
        if(dto.getIdx()!=null)
            target_idx = saveMsnDetailStatRepository.findBySaveMsn_Idx(dto.getIdx());


        result = new ArrayList<>(saveMsnDetailStatRepository.findAll());

        if(target_date!= null) {
            Set<Integer> idxSet = target_date.stream().map(SaveMsnDetailsStat::getTX).collect(Collectors.toSet());
            result = result.stream().filter(saveMsnDetailsStat -> idxSet.contains(saveMsnDetailsStat.getTX())).distinct().collect(Collectors.toList());
            changed = true;
        }
        if(target_serverUrl!= null) {
            Set<Integer> idxSet = target_serverUrl.stream().map(SaveMsnDetailsStat::getTX).collect(Collectors.toSet());
            result = result.stream().filter(answerMsnDetailsStat -> idxSet.contains(answerMsnDetailsStat.getTX())).distinct().collect(Collectors.toList());
            changed = true;
        }
        if(target_mediaCompany!= null) {
            Set<Integer> idxSet = target_mediaCompany.stream().map(SaveMsnDetailsStat::getTX).collect(Collectors.toSet());
            result = result.stream().filter(answerMsnDetailsStat -> idxSet.contains(answerMsnDetailsStat.getTX())).distinct().collect(Collectors.toList());
            changed = true;
        }



        if(target_isAbuse!= null) {
            Set<Integer> idxSet = target_isAbuse.stream().map(SaveMsnDetailsStat::getTX).collect(Collectors.toSet());
            result = result.stream().filter(saveMsnDetailsStat-> idxSet.contains(saveMsnDetailsStat.getTX())).distinct().collect(Collectors.toList());
            changed = true;
        }

        if(target_response!= null) {
            Set<Integer> idxSet = target_response.stream().map(SaveMsnDetailsStat::getTX).collect(Collectors.toSet());
            result = result.stream().filter(saveMsnDetailsStat -> idxSet.contains(saveMsnDetailsStat.getTX())).distinct().collect(Collectors.toList());
            changed = true;
        }

        if(target_advertiser != null) {
            Set<Integer> idxSet = target_advertiser.stream().map(SaveMsnDetailsStat::getTX).collect(Collectors.toSet());
            result = result.stream().filter(saveMsnDetailsStat -> idxSet.contains(saveMsnDetailsStat.getTX())).distinct().collect(Collectors.toList());
            changed = true;
        }
        if(target_idx != null) {
            Set<Integer> idxSet = target_idx.stream().map(SaveMsnDetailsStat::getTX).collect(Collectors.toSet());
            result = result.stream().filter(saveMsnDetailsStat -> idxSet.contains(saveMsnDetailsStat.getTX())).distinct().collect(Collectors.toList());
            changed = true;
        }
        if(dto.getSOrder()!=null) {
            changed = true;
            if (dto.getSOrder().equals("memberId")) {
                Map<Integer, SaveMsnDetailsStat> groupedResult = result.stream().collect(Collectors.toMap(
                        SaveMsnDetailsStat::getTX,
                        stat -> stat, // 값은 AnswerMsnDetailsStat 객체
                        (existing, replacement) -> {
                            // 날짜 비교하여 최신 것 선택
                            return existing.getRegistrationDate().isAfter(replacement.getRegistrationDate()) ? existing : replacement;
                        }
                ));
                result = groupedResult.values().stream().sorted(Comparator.comparing(SaveMsnDetailsStat::getRegistrationDate).reversed()).collect(Collectors.toList());
            }
        }
        if(!changed)
            result = new ArrayList<>();


        return result;
    }
}
