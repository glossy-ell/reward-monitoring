package com.example.reward_monitoring.general.advertiser.service;


import com.example.reward_monitoring.general.advertiser.dto.AdvertiserEditDto;
import com.example.reward_monitoring.general.advertiser.dto.AdvertiserReadDto;
import com.example.reward_monitoring.general.advertiser.dto.AdvertiserSearchDto;
import com.example.reward_monitoring.general.advertiser.entity.Advertiser;
import com.example.reward_monitoring.general.advertiser.repository.AdvertiserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
        List<Advertiser> target_date = null;
        List<Advertiser> target_is_active = null;
        List<Advertiser> target_advertiser =null;

        List<Advertiser> result;
        boolean changed = false;

        if(dto.getStartDate() != null || dto.getEndDate() != null){
            if(dto.getStartDate() != null){
                LocalDateTime start_time = dto.getStartDate().atStartOfDay();
                if(dto.getEndDate() == null){
                    target_date = advertiserRepository.findByStartDate(start_time);
                }else{
                    LocalDateTime end_time = dto.getEndDate().atTime(23,59);
                    target_date = advertiserRepository.findByBothDate(start_time,end_time);
                }

            }
            else {
                LocalDateTime end_time = dto.getEndDate().atTime(23,59);
                target_date = advertiserRepository.findByEndDate(end_time);
            }
        }

        if(dto.getIsActive() != null) {
            target_is_active = advertiserRepository.findByIsActive(dto.getIsActive());
        }
        else{
            target_is_active = advertiserRepository.findAll();
        }

        if(dto.getAdvertiser()!=null && !dto.getAdvertiser().isEmpty()){
            target_advertiser=advertiserRepository.findByAdvertiser(dto.getAdvertiser());
        }
        result = new ArrayList<>(advertiserRepository.findAll());

        if(target_date != null){
            Set<Integer> idxSet = target_date.stream().map(Advertiser::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(Advertiser -> idxSet.contains(Advertiser.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }

        if(target_is_active != null){
            Set<Integer> idxSet = target_is_active.stream().map(Advertiser::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(Advertiser -> idxSet.contains(Advertiser.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }

        if(target_advertiser != null){
            Set<Integer> idxSet = target_advertiser.stream().map(Advertiser::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(Advertiser -> idxSet.contains(Advertiser.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }


        if(!changed)
            result = new ArrayList<>();
        return result;
    }
}
