package com.example.reward_monitoring.statistics.saveMsn.sum.service;



import com.example.reward_monitoring.statistics.answerMsnStat.sum.entity.AnswerMsnSumStat;
import com.example.reward_monitoring.statistics.saveMsn.sum.dto.SaveMsnSumStatSearchDto;
import com.example.reward_monitoring.statistics.saveMsn.sum.entity.SaveMsnSumStat;
import com.example.reward_monitoring.statistics.saveMsn.sum.repository.SaveMsnSumStatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SaveMsnSumStatService {

    @Autowired
    private SaveMsnSumStatRepository saveMsnSumStatRepository;

    public List<SaveMsnSumStat> getSaveMsnSumStats(){
        return saveMsnSumStatRepository.findAll();
    }

    public List<SaveMsnSumStat> searchSaveMsnSum(SaveMsnSumStatSearchDto dto) {



        List<SaveMsnSumStat> target_serverUrl = null;
        List<SaveMsnSumStat> target_advertiser = null;
        List<SaveMsnSumStat> target_mediaCompany= null;
        List<SaveMsnSumStat> target_date = null;


        List<SaveMsnSumStat> result;
        boolean changed = false;

        if(dto.getUrl() != null)
            target_serverUrl = saveMsnSumStatRepository.findByServer_ServerUrl(dto.getUrl());

        if(dto.getAdvertiser()!=null)
            target_advertiser = saveMsnSumStatRepository.findByAdvertiser_Advertiser(dto.getAdvertiser());

        if(dto.getMediacompany()!=null)
            target_mediaCompany = saveMsnSumStatRepository.findByMediaCompany_CompanyName(dto.getMediacompany());

        if(dto.getStartAt() != null || dto.getEndAt() != null){
            if(dto.getStartAt() != null){
                if(dto.getEndAt() == null)
                    target_date = saveMsnSumStatRepository.findByStartAt(dto.getStartAt());
                else
                    target_date = saveMsnSumStatRepository.findByBothAt(dto.getStartAt(),dto.getEndAt());
            }
            else
                target_date = saveMsnSumStatRepository.findByEndAt(dto.getEndAt());
        }
        result = new ArrayList<>(saveMsnSumStatRepository.findAll());

        if(target_serverUrl!= null) {
            Set<Integer> idxSet = target_serverUrl.stream().map(SaveMsnSumStat::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(answerMsnSumStat -> idxSet.contains(answerMsnSumStat.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }
        if(target_advertiser!= null) {
            Set<Integer> idxSet = target_advertiser.stream().map(SaveMsnSumStat::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(saveMsnSumStat -> idxSet.contains(saveMsnSumStat.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }
        if(target_mediaCompany!= null) {
            Set<Integer> idxSet = target_mediaCompany.stream().map(SaveMsnSumStat::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(saveMsnSumStat -> idxSet.contains(saveMsnSumStat.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }
        if(target_date!= null) {
            Set<Integer> idxSet = target_date.stream().map(SaveMsnSumStat::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(saveMsnSumStat -> idxSet.contains(saveMsnSumStat.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }
        if(!changed)
            result = new ArrayList<>();
        return result;
    }
}
