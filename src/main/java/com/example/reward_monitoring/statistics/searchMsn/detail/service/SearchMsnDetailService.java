package com.example.reward_monitoring.statistics.searchMsn.detail.service;


import com.example.reward_monitoring.statistics.saveMsn.detail.dto.SaveMsnDetailSearchDto;
import com.example.reward_monitoring.statistics.saveMsn.detail.entity.SaveMsnDetailsStat;
import com.example.reward_monitoring.statistics.saveMsn.detail.repository.SaveMsnDetailStatRepository;
import com.example.reward_monitoring.statistics.searchMsn.detail.dto.SearchMsnDetailSearchDto;
import com.example.reward_monitoring.statistics.searchMsn.detail.entity.SearchMsnDetailsStat;
import com.example.reward_monitoring.statistics.searchMsn.detail.repository.SearchMsnDetailStatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchMsnDetailService {

    @Autowired
    private SearchMsnDetailStatRepository searchMsnDetailStatRepository;

    public List<SearchMsnDetailsStat> getSearchMsnsDetails() {
        return searchMsnDetailStatRepository.findAll();
    }

    public List<SearchMsnDetailsStat> searchSearchMsnDetail(SearchMsnDetailSearchDto dto) {

        List<SearchMsnDetailsStat> target_idx;
        List<SearchMsnDetailsStat> result = new ArrayList<>();

        if(dto.getUrl()!=null){
            result.addAll(searchMsnDetailStatRepository.findByServer_ServerUrl(dto.getUrl()));
        }
        if(dto.getStartAt() != null || dto.getEndAt() != null)
            if(dto.getStartAt() != null) {
                if (dto.getEndAt() == null)
                    result.addAll(searchMsnDetailStatRepository.findByStartAt(dto.getStartAt()));
                else
                    result.addAll(searchMsnDetailStatRepository.findByBothAt(dto.getStartAt(), dto.getEndAt()));
            }
            else
                result.addAll(searchMsnDetailStatRepository.findByEndAt(dto.getEndAt()));
        if(dto.getMediacompany()!=null)
            result.addAll(searchMsnDetailStatRepository.findByMediaCompany_companyName(dto.getMediacompany()));
        if(dto.getIsAbuse()!=null)
            result.addAll(searchMsnDetailStatRepository.findByIsAbuse(dto.getIsAbuse()));
        if(dto.getResponse()!=null)
            result.addAll(searchMsnDetailStatRepository.findByResponse(dto.getResponse()));
        if(dto.getAdvertiser()!=null)
            result.addAll(searchMsnDetailStatRepository.findByAdvertiser_Advertiser(dto.getAdvertiser()));
        if(dto.getIdx()!=null)
            result.addAll(searchMsnDetailStatRepository.findBySearchMsn_Idx(dto.getIdx()));


        return result.stream().distinct().collect(Collectors.toList());
    }
}
