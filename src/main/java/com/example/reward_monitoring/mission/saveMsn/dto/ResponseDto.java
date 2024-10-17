package com.example.reward_monitoring.mission.saveMsn.dto;

import com.example.reward_monitoring.mission.saveMsn.entity.SaveMsn;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ResponseDto {
    private List<SaveMsn> innerSaveMsns;


}
