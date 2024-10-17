package com.example.reward_monitoring.mission.answerMsn.dto;

import com.example.reward_monitoring.mission.answerMsn.entity.AnswerMsn;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ResponseDto {
    private List<AnswerMsn> innerAnswerMsns;


}
