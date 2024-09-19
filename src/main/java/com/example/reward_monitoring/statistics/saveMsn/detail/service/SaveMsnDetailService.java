package com.example.reward_monitoring.statistics.saveMsn.detail.service;


import com.example.reward_monitoring.statistics.saveMsn.detail.dto.SaveMsnDetailSearchDto;
import com.example.reward_monitoring.statistics.saveMsn.detail.entity.SaveMsnDetailsStat;
import com.example.reward_monitoring.statistics.saveMsn.detail.repository.SaveMsnDetailStatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SaveMsnDetailService {

    @Autowired
    private SaveMsnDetailStatRepository saveMsnDetailStatRepository;

    public List<SaveMsnDetailsStat> getSaveMsnsDetails() {
        return saveMsnDetailStatRepository.findAll();
    }

    public List<SaveMsnDetailsStat> searchSaveMsnDetail(SaveMsnDetailSearchDto dto) {

        List<SaveMsnDetailsStat> target_idx;
        List<SaveMsnDetailsStat> result = new ArrayList<>();

        if(dto.getUrl()!=null){
            result.addAll(saveMsnDetailStatRepository.findByServer_ServerUrl(dto.getUrl()));
        }
        if(dto.getStartAt() != null || dto.getEndAt() != null)
            if(dto.getStartAt() != null) {
                if (dto.getEndAt() == null)
                    result.addAll(saveMsnDetailStatRepository.findByStartAt(dto.getStartAt()));
                else
                    result.addAll(saveMsnDetailStatRepository.findByBothAt(dto.getStartAt(), dto.getEndAt()));
            }
            else
                result.addAll(saveMsnDetailStatRepository.findByEndAt(dto.getEndAt()));
        if(dto.getMediacompany()!=null)
            result.addAll(saveMsnDetailStatRepository.findByMediaCompany_companyName(dto.getMediacompany()));
        if(dto.getIsAbuse()!=null)
            result.addAll(saveMsnDetailStatRepository.findByIsAbuse(dto.getIsAbuse()));
        if(dto.getResponse()!=null)
            result.addAll(saveMsnDetailStatRepository.findByResponse(dto.getResponse()));
        if(dto.getAdvertiser()!=null)
            result.addAll(saveMsnDetailStatRepository.findByAdvertiser_Advertiser(dto.getAdvertiser()));
        if(dto.getIdx()!=null)
            result.addAll(saveMsnDetailStatRepository.findBySaveMsn_Idx(dto.getIdx()));


        return result.stream().distinct().collect(Collectors.toList());
    }
}
