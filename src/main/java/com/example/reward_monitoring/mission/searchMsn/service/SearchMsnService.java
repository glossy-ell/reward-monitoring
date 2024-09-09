package com.example.reward_monitoring.mission.searchMsn.service;


import com.example.reward_monitoring.mission.searchMsn.dto.SearchMsnEditDto;
import com.example.reward_monitoring.mission.searchMsn.dto.SearchMsnReadDto;
import com.example.reward_monitoring.mission.searchMsn.entity.SearchMsn;
import com.example.reward_monitoring.mission.searchMsn.repository.SearchMsnRepositroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchMsnService {
    @Autowired
    private SearchMsnRepositroy searchMsnRepositroy;

    public SearchMsn edit(int idx, SearchMsnEditDto dto) {
        SearchMsn searchMsn =searchMsnRepositroy.findByIdx(idx);
        if(searchMsn==null)
            return null;
        if(dto.getMissionDefaultQty() != null)
            searchMsn.setMissionDefaultQty(dto.getMissionDefaultQty());
        if(dto.getMissionDailyCap() !=null)
            searchMsn.setMissionDailyCap(dto.getMissionDailyCap());
        if(dto.getMissionTitle()!=null)
            searchMsn.setMissionTitle(dto.getMissionTitle());
        if(dto.getSearchKeyword()!=null)
            searchMsn.setSearchKeyword(dto.getSearchKeyword());
        if (dto.getStartAtMsn() != null)
            searchMsn.setStartAtMsn(dto.getStartAtMsn());
        if (dto.getEndAtMsn() != null)
            searchMsn.setEndAtMsn(dto.getEndAtMsn());
        if (dto.getStartAtCap() != null)
            searchMsn.setStartAtCap(dto.getStartAtCap());
        if (dto.getEndAtCap() != null)
            searchMsn.setEndAtCap(dto.getEndAtCap());
        if (dto.getMissionActive() != null) {
            boolean bool = dto.getMissionActive();
            searchMsn.setMissionActive(bool);
        }
        if (dto.getMissionExposure() != null) {
            boolean bool = dto.getMissionExposure();
            searchMsn.setMissionExposure(bool);
        }
        if (dto.getDupParticipation() != null) {
            boolean bool = dto.getDupParticipation();
            searchMsn.setDupParticipation(bool);
        }
        if (dto.getReEngagementDay() != null) {
            searchMsn.setReEngagementDay(dto.getReEngagementDay());
        }
        return searchMsn;
    }

    public SearchMsn add(SearchMsnReadDto dto) {
        return dto.toEntity();
    }

    public SearchMsn getSearchMsn(int idx) {
        return searchMsnRepositroy.findByIdx(idx);
    }

    public List<SearchMsn> getSearchMsns() {
        return searchMsnRepositroy.findAll();
    }

    public SearchMsn delete(int idx) {
        SearchMsn target = searchMsnRepositroy.findByIdx(idx);
        if(target==null)
            return null;
        searchMsnRepositroy.delete(target);
        return target;
    }
}
