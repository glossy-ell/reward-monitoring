package com.example.reward_monitoring.general.advertiser.service;


import com.example.reward_monitoring.general.advertiser.dto.AdvertiserEditDto;
import com.example.reward_monitoring.general.advertiser.dto.AdvertiserReadDto;
import com.example.reward_monitoring.general.advertiser.entity.Advertiser;
import com.example.reward_monitoring.general.advertiser.repository.AdvertiserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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


}
