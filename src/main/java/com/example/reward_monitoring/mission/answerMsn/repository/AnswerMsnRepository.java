package com.example.reward_monitoring.mission.answerMsn.repository;

import com.example.reward_monitoring.mission.answerMsn.entity.AnswerMsn;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerMsnRepository extends JpaRepository<AnswerMsn,Integer> {
    public AnswerMsn findByIdx(int idx);

}
