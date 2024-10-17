package com.example.reward_monitoring.mission.searchMsn.dto;

import com.example.reward_monitoring.mission.searchMsn.entity.SearchMsn;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ResponseDto {
    private List<SearchMsn> innerSearchMsns;


}
