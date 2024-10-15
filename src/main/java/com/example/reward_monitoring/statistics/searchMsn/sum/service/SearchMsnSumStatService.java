package com.example.reward_monitoring.statistics.searchMsn.sum.service;



import com.example.reward_monitoring.statistics.answerMsnStat.sum.entity.AnswerMsnSumStat;
import com.example.reward_monitoring.statistics.searchMsn.sum.dto.SearchMsnSumStatSearchDto;
import com.example.reward_monitoring.statistics.searchMsn.sum.entity.SearchMsnSumStat;
import com.example.reward_monitoring.statistics.searchMsn.sum.repository.SearchMsnSumStatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SearchMsnSumStatService {

    @Autowired
    private SearchMsnSumStatRepository searchMsnSumStatRepository;

    public List<SearchMsnSumStat> getSearchMsnSumStats(){
        return searchMsnSumStatRepository.findAll();
    }

    public List<SearchMsnSumStat> searchSearchMsnSum(SearchMsnSumStatSearchDto dto) {
        List<SearchMsnSumStat> target_serverUrl = null;
        List<SearchMsnSumStat> target_advertiser = null;
        List<SearchMsnSumStat> target_mediaCompany= null;
        List<SearchMsnSumStat> target_date = null;


        List<SearchMsnSumStat> result;
        boolean changed = false;

        if(dto.getUrl() != null)
            target_serverUrl = searchMsnSumStatRepository.findByServer_ServerUrl(dto.getUrl());

        if(dto.getAdvertiser()!=null)
            target_advertiser = searchMsnSumStatRepository.findByAdvertiser_Advertiser(dto.getAdvertiser());

        if(dto.getMediacompany()!=null)
            target_mediaCompany = searchMsnSumStatRepository.findByMediaCompany_CompanyName(dto.getMediacompany());

        if(dto.getStartAt() != null || dto.getEndAt() != null){
            if(dto.getStartAt() != null){
                if(dto.getEndAt() == null)
                    target_date = searchMsnSumStatRepository.findByStartAt(dto.getStartAt());
                else
                    target_date = searchMsnSumStatRepository.findByBothAt(dto.getStartAt(),dto.getEndAt());
            }
            else
                target_date = searchMsnSumStatRepository.findByEndAt(dto.getEndAt());
        }
        result = new ArrayList<>(searchMsnSumStatRepository.findAll());

        if(target_serverUrl!= null) {
            Set<Integer> idxSet = target_serverUrl.stream().map(SearchMsnSumStat::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(answerMsnSumStat -> idxSet.contains(answerMsnSumStat.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }
        if(target_advertiser!= null) {
            Set<Integer> idxSet = target_advertiser.stream().map(SearchMsnSumStat::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(answerMsnSumStat -> idxSet.contains(answerMsnSumStat.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }
        if(target_mediaCompany!= null) {
            Set<Integer> idxSet = target_mediaCompany.stream().map(SearchMsnSumStat::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(answerMsnSumStat -> idxSet.contains(answerMsnSumStat.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }
        if(target_date!= null) {
            Set<Integer> idxSet = target_date.stream().map(SearchMsnSumStat::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(answerMsnSumStat -> idxSet.contains(answerMsnSumStat.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }
        if(!changed)
            result = new ArrayList<>();
        return result;
    }
}
