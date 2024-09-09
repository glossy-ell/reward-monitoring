package com.example.reward_monitoring.general.advertiser.service;


import com.example.reward_monitoring.general.advertiser.dto.AdvertiserEditDto;
import com.example.reward_monitoring.general.advertiser.dto.AdvertiserReadDto;
import com.example.reward_monitoring.general.advertiser.dto.AdvertiserSearchDto;
import com.example.reward_monitoring.general.advertiser.entity.Advertiser;
import com.example.reward_monitoring.general.advertiser.repository.AdvertiserRepository;
import com.example.reward_monitoring.general.member.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
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
        return advertiser;
    }
    public Advertiser getAdvertiser(int idx) {
        return advertiserRepository.findByIdx(idx);
    }
    public List<Advertiser> getAdvertisers() {
        return advertiserRepository.findAll();
    }


    public List<Advertiser> searchAdvertiser(AdvertiserSearchDto dto) {
        List<Advertiser> target_date=null;
        List<Advertiser> target_is_active=null;
        List<Advertiser> target_advertiser=null;
        List<Advertiser> result=null;

        if(dto.getStartDate() != null || dto.getEndDate() != null){
            if(dto.getStartDate() != null){
                if(dto.getEndDate() == null){
                    ZoneId zoneId = ZoneId.of("Asia/Seoul");
                    ZonedDateTime start_time = dto.getStartDate().atStartOfDay(zoneId);
                    target_date = advertiserRepository.findByStartDate(start_time);
                }else{
                    ZoneId zoneId = ZoneId.of("Asia/Seoul");
                    ZonedDateTime start_time = dto.getStartDate().atStartOfDay(zoneId);
                    ZonedDateTime end_time = dto.getStartDate().atStartOfDay(zoneId);

                    target_date = advertiserRepository.findByBothDate(start_time,end_time);
                }

            }
            else {
                ZoneId zoneId = ZoneId.of("Asia/Seoul");
                ZonedDateTime end_time = dto.getEndDate().atStartOfDay(zoneId);

                target_date = advertiserRepository.findByEndDate(end_time);
            }

        }
        if(dto.getIsActive() != null){
            target_is_active = advertiserRepository.findByIsActive(dto.getIsActive());
        }
        if(dto.getAdvertiser()!=null){
            target_advertiser = advertiserRepository.findByAdvertiser(dto.getAdvertiser());
        }


        return result;
    }
}
