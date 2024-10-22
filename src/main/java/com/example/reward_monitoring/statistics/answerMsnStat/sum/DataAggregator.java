package com.example.reward_monitoring.statistics.answerMsnStat.sum;
import com.example.reward_monitoring.statistics.answerMsnStat.sum.Service.AnswerMsnSumStatService;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;

@Component
public class DataAggregator {

    private final AnswerMsnSumStatService answerMsnSumStatService;

    public DataAggregator(AnswerMsnSumStatService answerMsnSumStatService) {
        this.answerMsnSumStatService = answerMsnSumStatService;
    }


//    @Scheduled(cron = "0 1 0 * * ?")
//    public void aggregateYesterdayData() {
//        LocalDate yesterday = LocalDate.now().minusDays(1);
//
//        // 전날 데이터를 가져와서 처리하는 서비스 로직 호출
//        answerMsnSumStatService.aggregateDataForDate(yesterday);
//    }
}
