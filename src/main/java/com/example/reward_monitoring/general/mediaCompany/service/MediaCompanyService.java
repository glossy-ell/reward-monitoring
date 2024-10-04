package com.example.reward_monitoring.general.mediaCompany.service;


import com.example.reward_monitoring.general.mediaCompany.dto.MediaCompanyEditDto;
import com.example.reward_monitoring.general.mediaCompany.dto.MediaCompanyReadDto;
import com.example.reward_monitoring.general.mediaCompany.dto.MediaCompanySearchDto;
import com.example.reward_monitoring.general.mediaCompany.entity.MediaCompany;
import com.example.reward_monitoring.general.mediaCompany.repository.MediaCompanyRepository;
import com.example.reward_monitoring.general.member.repository.MemberRepository;
import com.example.reward_monitoring.general.userServer.RandomKeyGenerator;
import com.example.reward_monitoring.general.userServer.entity.Server;
import com.example.reward_monitoring.general.userServer.repository.ServerRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MediaCompanyService {

    @Autowired
    private MediaCompanyRepository mediaCompanyRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ServerRepository serverRepository;


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
        if(dto.getType()!=null)
            mediaCompany.setType(dto.getType());
        if(dto.getCompanyReturnUrl()!=null)
            mediaCompany.setCompanyReturnUrl(dto.getCompanyReturnUrl());
        if(dto.getCompanyReturnParameter()!=null)
            mediaCompany.setCompanyReturnParameter(dto.getCompanyReturnParameter());
        if(dto.getCompanyUserSaving()!=null) {
            Gson gson = new Gson();
            Map<String,Integer> data = gson.fromJson(dto.getCompanyUserSaving(), new TypeToken<Map<String, Integer>>(){}.getType());
            if(data.get("quiz")!=null) {
                mediaCompany.setCompanyUserSavingQuiz(data.get("quiz"));
            }
            if(data.get("search")!=null)
                mediaCompany.setCompanyUserSavingSearch(data.get("search"));
            if(data.get("sightseeing")!=null)
                mediaCompany.setCompanyUserSavingSightseeing(data.get("sightseeing"));
        }

        if(dto.getMemo()!=null)
            mediaCompany.setMemo(dto.getMemo());
        mediaCompanyRepository.save(mediaCompany);
        return mediaCompany;
    }

    public MediaCompany add(MediaCompanyReadDto dto) {
        Gson gson = new Gson();
        log.info(dto.getCompanyUserSaving());
        if(dto.getCompanyUserSaving() !=null) {
            Map<String, Integer> data = gson.fromJson(dto.getCompanyUserSaving(), new TypeToken<Map<String, Integer>>() {}.getType());
            if (data.get("quiz") != null)
                dto.setCompanyUserSavingQuiz(data.get("quiz"));
            if (data.get("search") != null)
                dto.setCompanyUserSavingSearch(data.get("search"));
            if (data.get("sightseeing") != null)
                dto.setCompanyUserSavingSightseeing(data.get("sightseeing"));
        }
        String key = RandomKeyGenerator.generateRandomKey(15);
        dto.setApiKey(key);

        Server server = serverRepository.findByServerUrl_(dto.getServerUrl());
        return dto.toEntity(server);
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

    public List<MediaCompany> searchMediaCompany(MediaCompanySearchDto dto) {
        List<MediaCompany>  target_date;
        List<MediaCompany>  target_is_active;
        List<MediaCompany> target_operation_type;
        List<MediaCompany> target_name;
        List<MediaCompany> target_api;
        List<MediaCompany> result = new ArrayList<>();


        if(dto.getStartDate() != null || dto.getEndDate() != null){
            if(dto.getStartDate() != null){
                ZoneId zoneId = ZoneId.of("Asia/Seoul");
                ZonedDateTime start_time = dto.getStartDate().atStartOfDay(zoneId);
                if(dto.getEndDate() == null){
                    target_date = mediaCompanyRepository.findByStartDate(start_time);
                    result.addAll(target_date);

                }else{
                    ZonedDateTime end_time = dto.getEndDate().atStartOfDay(zoneId);
                    target_date = mediaCompanyRepository.findByBothDate(start_time,end_time);
                    result.addAll(target_date);
                }

            }
            else {
                ZoneId zoneId = ZoneId.of("Asia/Seoul");
                ZonedDateTime end_time = dto.getEndDate().atStartOfDay(zoneId);

                target_date = mediaCompanyRepository.findByEndDate(end_time);
                result.addAll(target_date);
            }

        }
        if(dto.getIsActive() != null){
            target_is_active = mediaCompanyRepository.findByIsActive(dto.getIsActive());
            if(result.isEmpty())
                result.addAll(target_is_active);
            else{
                result.retainAll(target_is_active);
            }
        }
        if(dto.getOperationType()!=null){
            target_operation_type = mediaCompanyRepository.findByOperationType(dto.getOperationType());
            if(result.isEmpty())
                result.addAll(target_operation_type);
            else{
                result.retainAll(target_operation_type);
            }
        }

        if(dto.getCompanyName()!=null){
            target_name = mediaCompanyRepository.findByName(dto.getCompanyName());
            if(result.isEmpty())
                result.addAll(target_name);
            else{
                result.retainAll(target_name);
            }
        }

        if(dto.getAPIKey()!=null){
            target_api = mediaCompanyRepository.findByApi(dto.getAPIKey());
            if(result.isEmpty())
                result.addAll(target_api);
            else{
                result.retainAll(target_api);
            }
        }


        return result.stream().distinct().collect(Collectors.toList());

    }
}
