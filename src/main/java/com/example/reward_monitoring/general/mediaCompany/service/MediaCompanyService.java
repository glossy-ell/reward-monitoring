package com.example.reward_monitoring.general.mediaCompany.service;

import com.example.reward_monitoring.general.advertiser.entity.Advertiser;
import com.example.reward_monitoring.general.mediaCompany.dto.MediaCompanyEditDto;
import com.example.reward_monitoring.general.mediaCompany.dto.MediaCompanyReadDto;
import com.example.reward_monitoring.general.mediaCompany.entity.MediaCompany;
import com.example.reward_monitoring.general.mediaCompany.repository.MediaCompanyRepository;
import com.example.reward_monitoring.general.member.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MediaCompanyService {

    @Autowired
    private MediaCompanyRepository mediaCompanyRepository;



    public MediaCompany edit(int idx,MediaCompanyEditDto dto) {
        MediaCompany mediaCompany = mediaCompanyRepository.findByIdx(idx);

        if(mediaCompany ==null)
            return null;

        if(dto.getCompanyName()!=null)
            mediaCompany.setCompanyName(dto.getCompanyName());
        if(dto.getCompanyID()!=null)
            mediaCompany.setCompanyID(dto.getCompanyID());
        if(dto.getCompanyManager()!=null)
            mediaCompany.setCompanyManager(dto.getCompanyManager());
        if(dto.getCompanyManagerPhoneNum()!=null)
            mediaCompany.setCompanyManagePhoneNum(dto.getCompanyManagerPhoneNum());
        if(dto.getAPIKey()!=null)
            mediaCompany.setAPIKey(dto.getAPIKey());
        if(dto.getIsActive() !=null) {
            boolean bool = dto.getIsActive();
            mediaCompany.setActive(bool);
        }

        return mediaCompany;
    }

    public MediaCompany add(MediaCompanyReadDto dto) {
        return dto.toEntity();
    }


    public MediaCompany getMediaCompany(int idx) {  // idx 검색
        return mediaCompanyRepository.findByIdx(idx);
    }

    public List<MediaCompany> getMediaCompanys() { // 리스트 검색
        return mediaCompanyRepository.findAll();
    }


    public MediaCompany delete(int idx) {
        MediaCompany target = mediaCompanyRepository.findByIdx(idx);
        if(target == null)
            return null;
        mediaCompanyRepository.delete(target);
        return target;
    }
}
