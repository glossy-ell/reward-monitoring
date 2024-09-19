package com.example.reward_monitoring.statistics.saveMsn.sum.service;



import com.example.reward_monitoring.statistics.saveMsn.sum.dto.SaveMsnSumStatSearchDto;
import com.example.reward_monitoring.statistics.saveMsn.sum.entity.SaveMsnSumStat;
import com.example.reward_monitoring.statistics.saveMsn.sum.repository.SaveMsnSumStatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SaveMsnSumStatService {

    @Autowired
    private SaveMsnSumStatRepository saveMsnSumStatRepository;

    public List<SaveMsnSumStat> getSaveMsnSumStats(){
        return saveMsnSumStatRepository.findAll();
    }

    public List<SaveMsnSumStat> searchSaveMsnSum(SaveMsnSumStatSearchDto dto) {



        List<SaveMsnSumStat> result = new ArrayList<>();
        if(dto.getUrl() != null)
            result.addAll(saveMsnSumStatRepository.findByServer_ServerUrl(dto.getUrl()));

        if(dto.getAdvertiser()!=null)
            result.addAll(saveMsnSumStatRepository.findByAdvertiser_Advertiser(dto.getAdvertiser()));

        if(dto.getMediacompany()!=null)
            result.addAll(saveMsnSumStatRepository.findByMediaCompany_CompanyName(dto.getMediacompany()));

        if(dto.getStartAt() != null || dto.getEndAt() != null){
            if(dto.getStartAt() != null){
                if(dto.getEndAt() == null)
                    result.addAll(saveMsnSumStatRepository.findByStartAt(dto.getStartAt()));
                else
                    result.addAll(saveMsnSumStatRepository.findByBothAt(dto.getStartAt(),dto.getEndAt()));

            }
            else
                result.addAll(saveMsnSumStatRepository.findByEndAt(dto.getEndAt()));
        }

        return result.stream().distinct().collect(Collectors.toList());
    }
}
