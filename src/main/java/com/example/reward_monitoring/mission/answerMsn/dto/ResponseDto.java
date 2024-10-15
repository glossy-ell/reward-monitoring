package com.example.reward_monitoring.mission.answerMsn.dto;

import com.example.reward_monitoring.mission.answerMsn.entity.AnswerMsn;

import java.util.List;

public class ResponseDto {
    private List<AnswerMsn> answerMsns; // AnswerMsn은 실제 데이터 구조에 맞게 조정


    public List<AnswerMsn> getAnswerMsns() {
        return answerMsns;
    }

    public void setAnswerMsns(List<AnswerMsn> answerMsns) {
        this.answerMsns = answerMsns;
    }
}
