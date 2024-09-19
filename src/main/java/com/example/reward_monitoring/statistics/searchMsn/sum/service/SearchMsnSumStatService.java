package com.example.reward_monitoring.statistics.searchMsn.sum.service;



import com.example.reward_monitoring.statistics.searchMsn.sum.dto.SearchMsnSumStatSearchDto;
import com.example.reward_monitoring.statistics.searchMsn.sum.entity.SearchMsnSumStat;
import com.example.reward_monitoring.statistics.searchMsn.sum.repository.SearchMsnSumStatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchMsnSumStatService {

    @Autowired
    private SearchMsnSumStatRepository searchMsnSumStatRepository;

    public List<SearchMsnSumStat> getSearchMsnSumStats(){
        return searchMsnSumStatRepository.findAll();
    }

    public List<SearchMsnSumStat> searchSearchMsnSum(SearchMsnSumStatSearchDto dto) {



        List<SearchMsnSumStat> result = new ArrayList<>();
        if(dto.getUrl() != null)
            result.addAll(searchMsnSumStatRepository.findByServer_ServerUrl(dto.getUrl()));

        if(dto.getAdvertiser()!=null)
            result.addAll(searchMsnSumStatRepository.findByAdvertiser_Advertiser(dto.getAdvertiser()));

        if(dto.getMediacompany()!=null)
            result.addAll(searchMsnSumStatRepository.findByMediaCompany_CompanyName(dto.getMediacompany()));

        if(dto.getStartAt() != null || dto.getEndAt() != null){
            if(dto.getStartAt() != null){
                if(dto.getEndAt() == null)
                    result.addAll(searchMsnSumStatRepository.findByStartAt(dto.getStartAt()));
                else
                    result.addAll(searchMsnSumStatRepository.findByBothAt(dto.getStartAt(),dto.getEndAt()));

            }
            else
                result.addAll(searchMsnSumStatRepository.findByEndAt(dto.getEndAt()));
        }

        return result.stream().distinct().collect(Collectors.toList());
    }
}
