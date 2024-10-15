package com.example.reward_monitoring.mission.searchMsn.dto;

import com.example.reward_monitoring.mission.answerMsn.entity.AnswerMsn;
import com.example.reward_monitoring.mission.searchMsn.entity.SearchMsn;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ResponseDto {
    private List<SearchMsn> innerAnswerMsns; // AnswerMsn은 실제 데이터 구조에 맞게 조정


}
