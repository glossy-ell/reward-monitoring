package com.example.reward_monitoring.statistics.answerMsnStat.sum.Service;


import com.example.reward_monitoring.statistics.answerMsnStat.daily.entity.AnswerMsnDailyStat;
import com.example.reward_monitoring.statistics.answerMsnStat.daily.repository.AnswerMsnDailyStatRepository;
import com.example.reward_monitoring.statistics.answerMsnStat.sum.dto.AnswerMsnSumStatSearchDto;
import com.example.reward_monitoring.statistics.answerMsnStat.sum.entity.AnswerMsnSumStat;
import com.example.reward_monitoring.statistics.answerMsnStat.sum.repository.AnswerMsnSumStatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AnswerMsnSumStatService {

    @Autowired
    private AnswerMsnSumStatRepository answerMsnSumStatRepository;

    @Autowired
    private AnswerMsnDailyStatRepository answerMsnDailyStatRepository;

    public List<AnswerMsnSumStat> getAnswerMsnSumStats(){
        return answerMsnSumStatRepository.findAll();
    }

    public List<AnswerMsnSumStat> searchAnswerMsnSum(AnswerMsnSumStatSearchDto dto) {

        List<AnswerMsnSumStat> target_serverUrl = null;
        List<AnswerMsnSumStat> target_advertiser = null;
        List<AnswerMsnSumStat> target_mediaCompany= null;
        List<AnswerMsnSumStat> target_date = null;


        List<AnswerMsnSumStat> result;
        boolean changed = false;

        if(dto.getUrl() != null)
            target_serverUrl = answerMsnSumStatRepository.findByServer_ServerUrl(dto.getUrl());

        if(dto.getAdvertiser()!=null)
            target_advertiser = answerMsnSumStatRepository.findByAdvertiser_Advertiser(dto.getAdvertiser());

        if(dto.getMediacompany()!=null)
            target_mediaCompany = answerMsnSumStatRepository.findByMediaCompany_CompanyName(dto.getMediacompany());

        if(dto.getStartAt() != null || dto.getEndAt() != null){
            if(dto.getStartAt() != null){
                if(dto.getEndAt() == null)
                    target_date = answerMsnSumStatRepository.findByStartAt(dto.getStartAt());
                else
                    target_date = answerMsnSumStatRepository.findByBothAt(dto.getStartAt(),dto.getEndAt());
            }
            else
                target_date = answerMsnSumStatRepository.findByEndAt(dto.getEndAt());
        }
        result = new ArrayList<>(answerMsnSumStatRepository.findAll());

        if(target_serverUrl!= null) {
            Set<Integer> idxSet = target_serverUrl.stream().map(AnswerMsnSumStat::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(answerMsnSumStat -> idxSet.contains(answerMsnSumStat.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }
        if(target_advertiser!= null) {
            Set<Integer> idxSet = target_advertiser.stream().map(AnswerMsnSumStat::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(answerMsnSumStat -> idxSet.contains(answerMsnSumStat.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }
        if(target_mediaCompany!= null) {
            Set<Integer> idxSet = target_mediaCompany.stream().map(AnswerMsnSumStat::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(answerMsnSumStat -> idxSet.contains(answerMsnSumStat.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }
        if(target_date!= null) {
            Set<Integer> idxSet = target_date.stream().map(AnswerMsnSumStat::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(answerMsnSumStat -> idxSet.contains(answerMsnSumStat.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }
        if(!changed)
            result = new ArrayList<>();
        return result;
    }

//    public void aggregateDataForDate(LocalDate date) {
//        // 전날의 데이터를 DB에서 조회
//        List<AnswerMsnDailyStat> data = answerMsnDailyStatRepository.findByDate(date);
//
//        // 데이터를 합산하는 로직 수행
//        int landCnt = data.stream().mapToInt(AnswerMsnDailyStat::getLandingCnt).sum();
//        int partCnt = data.stream().mapToInt(AnswerMsnDailyStat::getPartCnt).sum();
//        // 결과를 저장하거나 다른 처리 수행
//        saveAggregatedData(landCnt,partCnt, date);
//    }
//
//    private void saveAggregatedData(int landCnt, int partCnt, LocalDate date) {
//        // 합산된 결과를 저장하는 로직
//        AnswerMsnSumStat  answerMsnSumStat= new AnswerMsnSumStat();
//        answerMsnSumStat.setLandingCount(landCnt);
//        answerMsnSumStat.setPartCount(landCnt);
//        answerMsnSumStatRepository.save(answerMsnSumStat);
//    }
}
