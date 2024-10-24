package com.example.reward_monitoring.statistics.searchMsn.detail.service;


import com.example.reward_monitoring.statistics.searchMsn.detail.dto.SearchMsnDetailSearchDto;
import com.example.reward_monitoring.statistics.searchMsn.detail.entity.SearchMsnDetailsStat;
import com.example.reward_monitoring.statistics.searchMsn.detail.repository.SearchMsnDetailStatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchMsnDetailService {

    @Autowired
    private SearchMsnDetailStatRepository searchMsnDetailStatRepository;

    public List<SearchMsnDetailsStat> getSearchMsnsDetails() {
        return searchMsnDetailStatRepository.findAll();
    }

    public List<SearchMsnDetailsStat> searchSearchMsnDetail(SearchMsnDetailSearchDto dto) {

        List<SearchMsnDetailsStat> target_date = null;
        List<SearchMsnDetailsStat> target_serverUrl = null;
        List<SearchMsnDetailsStat> target_mediaCompany = null;


        List<SearchMsnDetailsStat> target_isAbuse = null;
        List<SearchMsnDetailsStat> target_response = null;
        List<SearchMsnDetailsStat> target_advertiser = null;
        List<SearchMsnDetailsStat> target_idx = null;
        List<SearchMsnDetailsStat>result;
        boolean changed = false;

        if(dto.getUrl()!=null){
            target_serverUrl = searchMsnDetailStatRepository.findByServer_ServerUrl(dto.getUrl());
        }
        if(dto.getStartAt() != null || dto.getEndAt() != null)
            if(dto.getStartAt() != null) {
                if (dto.getEndAt() == null)
                    target_date = searchMsnDetailStatRepository.findByStartAt(dto.getStartAt());
                else
                    target_date = searchMsnDetailStatRepository.findByBothAt(dto.getStartAt(), dto.getEndAt());
            }
            else
                target_date = searchMsnDetailStatRepository.findByEndAt(dto.getEndAt());

        if(dto.getMediacompany()!=null)
            target_mediaCompany = searchMsnDetailStatRepository.findByMediaCompany_companyName(dto.getMediacompany());
        if(dto.getIsAbuse()!=null)
            target_isAbuse = searchMsnDetailStatRepository.findByIsAbuse(dto.getIsAbuse());
        if(dto.getResponse()!=null)
            target_response = searchMsnDetailStatRepository.findByResponse(dto.getResponse());

        if(dto.getAdvertiser()!=null)
            target_advertiser = searchMsnDetailStatRepository.findByAdvertiser_Advertiser(dto.getAdvertiser());
        if(dto.getIdx()!=null)
            target_idx = searchMsnDetailStatRepository.findBySearchMsn_Idx(dto.getIdx());


        result = new ArrayList<>(searchMsnDetailStatRepository.findAll());

        if(target_date!= null) {
            Set<Integer> idxSet = target_date.stream().map(SearchMsnDetailsStat::getTX).collect(Collectors.toSet());
            result = result.stream().filter(searchDetailsStat -> idxSet.contains(searchDetailsStat.getTX())).distinct().collect(Collectors.toList());
            changed = true;
        }
        if(target_serverUrl!= null) {
            Set<Integer> idxSet = target_serverUrl.stream().map(SearchMsnDetailsStat::getTX).collect(Collectors.toSet());
            result = result.stream().filter(searchMsnDetailsStat -> idxSet.contains(searchMsnDetailsStat.getTX())).distinct().collect(Collectors.toList());
            changed = true;
        }
        if(target_mediaCompany!= null) {
            Set<Integer> idxSet = target_mediaCompany.stream().map(SearchMsnDetailsStat::getTX).collect(Collectors.toSet());
            result = result.stream().filter(searchMsnDetailsStat-> idxSet.contains(searchMsnDetailsStat.getTX())).distinct().collect(Collectors.toList());
            changed = true;
        }

        if(target_isAbuse!= null) {
            Set<Integer> idxSet = target_isAbuse.stream().map(SearchMsnDetailsStat::getTX).collect(Collectors.toSet());
            result = result.stream().filter(searchMsnDetailsStat -> idxSet.contains(searchMsnDetailsStat.getTX())).distinct().collect(Collectors.toList());
            changed = true;
        }

        if(target_response!= null) {
            Set<Integer> idxSet = target_response.stream().map(SearchMsnDetailsStat::getTX).collect(Collectors.toSet());
            result = result.stream().filter(searchMsnDetailsStat -> idxSet.contains(searchMsnDetailsStat.getTX())).distinct().collect(Collectors.toList());
            changed = true;
        }

        if(target_advertiser != null) {
            Set<Integer> idxSet = target_advertiser.stream().map(SearchMsnDetailsStat::getTX).collect(Collectors.toSet());
            result = result.stream().filter(searchMsnDetailsStat -> idxSet.contains(searchMsnDetailsStat.getTX())).distinct().collect(Collectors.toList());
            changed = true;
        }
        if(target_idx != null) {
            Set<Integer> idxSet = target_idx.stream().map(SearchMsnDetailsStat::getTX).collect(Collectors.toSet());
            result = result.stream().filter(searchMsnDetailsStat -> idxSet.contains(searchMsnDetailsStat.getTX())).distinct().collect(Collectors.toList());
            changed = true;
        }

        if(!changed)
            result = new ArrayList<>();
        if(dto.getSOrder().equals("memberId")){
            Map<Integer, SearchMsnDetailsStat> groupedResult = result.stream().collect(Collectors.toMap(
                    SearchMsnDetailsStat::getTX,
                    stat -> stat, // 값은 AnswerMsnDetailsStat 객체
                    (existing, replacement) -> {
                        // 날짜 비교하여 최신 것 선택
                        return existing.getRegistrationDate().isAfter(replacement.getRegistrationDate()) ? existing : replacement;
                    }
            ));
            result  = groupedResult.values().stream().sorted(Comparator.comparing(SearchMsnDetailsStat::getRegistrationDate).reversed()).collect(Collectors.toList());
        }

        return result;
    }
}
