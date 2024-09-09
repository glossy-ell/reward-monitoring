package com.example.reward_monitoring.general.mediaCompany.service;

import com.example.reward_monitoring.general.advertiser.entity.Advertiser;
import com.example.reward_monitoring.general.mediaCompany.dto.MediaCompanyEditDto;
import com.example.reward_monitoring.general.mediaCompany.dto.MediaCompanyReadDto;
import com.example.reward_monitoring.general.mediaCompany.dto.MediaCompanySearchDto;
import com.example.reward_monitoring.general.mediaCompany.entity.MediaCompany;
import com.example.reward_monitoring.general.mediaCompany.repository.MediaCompanyRepository;
import com.example.reward_monitoring.general.member.entity.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
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

    @Operation(summary = "관리자 검색", description = "조건에 맞는 관리자를 검색합니다")
    @PostMapping("/member/search")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
            @ApiResponse(responseCode = "500", description = "검색 중 예기치않은 오류발생")
    })
    public List<MediaCompany> searchMediaCompany(MediaCompanySearchDto dto) {
        List<MediaCompany>  target_date=null;
        List<MediaCompany>  target_is_active=null;
        List<MediaCompany> target_operation_type = null;
        List<MediaCompany> target_name=null;
        List<MediaCompany> target_api=null;
        List<MediaCompany> result=null;


        if(dto.getStartDate() != null || dto.getEndDate() != null){
            if(dto.getStartDate() != null){
                if(dto.getEndDate() == null){
                    ZoneId zoneId = ZoneId.of("Asia/Seoul");
                    ZonedDateTime start_time = dto.getStartDate().atStartOfDay(zoneId);
                    target_date = mediaCompanyRepository.findByStartDate(start_time);
                }else{
                    ZoneId zoneId = ZoneId.of("Asia/Seoul");
                    ZonedDateTime start_time = dto.getStartDate().atStartOfDay(zoneId);
                    ZonedDateTime end_time = dto.getStartDate().atStartOfDay(zoneId);

                    target_date = mediaCompanyRepository.findByBothDate(start_time,end_time);
                }

            }
            else {
                ZoneId zoneId = ZoneId.of("Asia/Seoul");
                ZonedDateTime end_time = dto.getEndDate().atStartOfDay(zoneId);

                target_date = mediaCompanyRepository.findByEndDate(end_time);
            }

        }
        if(dto.getIsActive() != null){
            target_is_active = mediaCompanyRepository.findByIsActive(dto.getIsActive());
        }
        if(dto.getOperationType()!=null){
            target_operation_type = mediaCompanyRepository.findByOperationType(dto.getOperationType());
        }

        if(dto.getName()!=null){
            target_name = mediaCompanyRepository.findByName(dto.getName());
        }

        if(dto.getApi()!=null){
            target_api = mediaCompanyRepository.findByApi(dto.getApi());
        }

        if(target_date!=null) {
            result = new ArrayList<>(target_date);
            if(target_is_active!=null)
                result.retainAll(target_is_active);
            if(target_operation_type!=null)
                result.retainAll(target_operation_type);
            if(target_name!=null)
                result.retainAll(target_name);
            if(target_api!=null)
                result.retainAll(target_api);
        }
        else if(target_is_active !=null){
            result = new ArrayList<>(target_is_active);

            if(target_operation_type!=null)
                result.retainAll(target_operation_type);
            if(target_name !=null)
                result.retainAll(target_name);
            if(target_api != null)
                result.retainAll(target_api);

        } else if (target_operation_type!=null) {
            result = new ArrayList<>(target_operation_type);
            if(target_name !=null)
                result.retainAll(target_name);
            if(target_api != null)
                result.retainAll(target_api);
        }
        else if(target_name != null){
            result = new ArrayList<>(target_name);
        } else if(target_api != null){
            result = new ArrayList<>(target_api);
        }
        return result;

    }
}
