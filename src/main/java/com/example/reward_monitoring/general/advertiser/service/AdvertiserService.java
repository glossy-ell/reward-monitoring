package com.example.reward_monitoring.general.advertiser.service;


import com.example.reward_monitoring.general.advertiser.dto.AdvertiserEditDto;
import com.example.reward_monitoring.general.advertiser.dto.AdvertiserReadDto;
import com.example.reward_monitoring.general.advertiser.dto.AdvertiserSearchDto;
import com.example.reward_monitoring.general.advertiser.entity.Advertiser;
import com.example.reward_monitoring.general.advertiser.repository.AdvertiserRepository;
import com.example.reward_monitoring.general.member.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequestMapping("/Advertiser")
public class AdvertiserService {

    @Autowired
    private AdvertiserRepository advertiserRepository;


    public Advertiser add(AdvertiserReadDto dto) {

        return dto.toEntity();
    }

    public Advertiser delete(int idx) {
        Advertiser target = advertiserRepository.findByIdx(idx);
        if(target == null)
            return null;
        advertiserRepository.delete(target);
        return target;
    }

    public Advertiser edit(int idx,AdvertiserEditDto dto) {
        Advertiser advertiser = advertiserRepository.findByIdx(idx);
        if(advertiser == null){
            return null;
        }

        if(dto.getAdvertiser() != null)
            advertiser.setAdvertiser(dto.getAdvertiser());
        if(dto.getManager() != null)
            advertiser.setManager(dto.getManager());
        if(dto.getManager() != null)
            advertiser.setAdvertiser(dto.getManagerPhoneNum());
        if(dto.getIsActive() !=null) {
            boolean bool = dto.getIsActive();
            advertiser.setActive(bool);
        }
        if(dto.getMemo() !=null){
            advertiser.setMemo(dto.getMemo());
        }
        return advertiser;
    }
    public Advertiser getAdvertiser(int idx) {
        return advertiserRepository.findByIdx(idx);
    }

    public List<Advertiser> getAdvertisers() {
        return advertiserRepository.findAll();
    }


    public List<Advertiser> searchAdvertiser(AdvertiserSearchDto dto) {
        List<Advertiser> target_date;
        List<Advertiser> target_is_active;
        List<Advertiser> target_advertiser;
        List<Advertiser> result= new ArrayList<>();

        if(dto.getStartDate() != null || dto.getEndDate() != null){
            if(dto.getStartDate() != null){
                ZoneId zoneId = ZoneId.of("Asia/Seoul");
                ZonedDateTime start_time = dto.getStartDate().atStartOfDay(zoneId);
                if(dto.getEndDate() == null){
                    result.addAll(advertiserRepository.findByStartDate(start_time));
                }else{
                    ZonedDateTime end_time = dto.getStartDate().atStartOfDay(zoneId);
                    result.addAll(advertiserRepository.findByBothDate(start_time,end_time));
                }

            }
            else {
                ZoneId zoneId = ZoneId.of("Asia/Seoul");
                ZonedDateTime end_time = dto.getEndDate().atStartOfDay(zoneId);
                result.addAll(advertiserRepository.findByEndDate(end_time));
            }

        }

        if(dto.getIsActive() != null) {
            target_is_active = advertiserRepository.findByIsActive(dto.getIsActive());
            if(result.isEmpty())
                result.addAll(target_is_active);
            else{
                result.retainAll(target_is_active);
            }
        }

        if(dto.getAdvertiser()!=null){
            target_advertiser=advertiserRepository.findByAdvertiser(dto.getAdvertiser());
            if(result.isEmpty())
                result.addAll(target_advertiser);
            else{
                result.retainAll(target_advertiser);
            }
        }
        return result.stream().distinct().collect(Collectors.toList());
    }
}
