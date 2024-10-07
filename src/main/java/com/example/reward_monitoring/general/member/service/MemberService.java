package com.example.reward_monitoring.general.member.service;


import com.example.reward_monitoring.general.member.dto.MemberEditDto;
import com.example.reward_monitoring.general.member.dto.MemberReadDto;
import com.example.reward_monitoring.general.member.dto.MemberSearchDto;
import com.example.reward_monitoring.general.member.entity.Member;
import com.example.reward_monitoring.general.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


    public Member join(MemberReadDto dto){
        Member member = dto.toEntity();
        String rawPassword = member.getPassword();
        String encPassword = passwordEncoder.encode(rawPassword);
        member.setPassword(encPassword);
        return member;
    }





    public Member edit(int idx,MemberEditDto dto){

        Member member = memberRepository.findByIdx(idx);
        if(member == null)
            return null;
        
        // 기본 회원정보 설정
        if(dto.getPassword()!=null)
            member.setPassword(passwordEncoder.encode(dto.getPassword()));
        if(dto.getName()!=null)
            member.setName(dto.getName());
        if(dto.getDepartment()!=null)
            member.setDepartment(dto.getDepartment());
        if(dto.getCtryCode()!=null)
            member.setCtryCode(dto.getCtryCode());
        if(dto.getPhone()!=null)
            member.setPhone(dto.getPhone());
        if(dto.getLang()!=null)
            member.setLang(dto.getLang());
        if(dto.getIsActive()!=null)
            member.setActive(dto.getIsActive());
        if(dto.getMemo()!=null)
            member.setMemo(dto.getMemo());
        if(dto.getNauthMember()!=null)
            member.setNauthMember(dto.getNauthMember());

        // 비권한 메뉴 관리
        if(dto.getNauthAnswerMsn()!=null)
            member.setNauthAnswerMsn(dto.getNauthAnswerMsn());
        if(dto.getNauthCurAnswer()!=null)
            member.setNauthCurAnswer(dto.getNauthCurAnswer());
        if(dto.getNauthAnswerUpload()!=null)
            member.setNauthAnswerUpload(dto.getNauthAnswerUpload());

        if (dto.getNauthSearchMsn() != null) {
            member.setNauthSearchMsn(dto.getNauthSearchMsn());
        }
        if (dto.getNauthCurSearch() != null) {
            member.setNauthCurSearch(dto.getNauthCurSearch());
        }
        if (dto.getNauthSearchUpload() != null) {
            member.setNauthSearchUpload(dto.getNauthSearchUpload());
        }
        
        if (dto.getNauthSaveMsn() != null) {
            member.setNauthSaveMsn(dto.getNauthSaveMsn());
        }
        if (dto.getNauthCurSave() != null) {
            member.setNauthCurSave(dto.getNauthCurSave());
        }
        if (dto.getNauthSaveUpload() != null) {
            member.setNauthSaveUpload(dto.getNauthSaveUpload());
        }
        
        if (dto.getNauthAnswerDaily() != null) {
            member.setNauthAnswerDaily(dto.getNauthAnswerDaily());
        }
        if (dto.getNauthAnswerDetail() != null) {
            member.setNauthAnswerDetail(dto.getNauthAnswerDetail());
        }
        if (dto.getNauthAnswerSum() != null) {
            member.setNauthAnswerSum(dto.getNauthAnswerSum());
        }
        
        if (dto.getNauthSearchDaily() != null) {
            member.setNauthSearchDaily(dto.getNauthSearchDaily());
        }
        if (dto.getNauthSearchDetail() != null) {
            member.setNauthSearchDetail(dto.getNauthSearchDetail());
        }
        if (dto.getNauthSearchSum() != null) {
            member.setNauthSearchSum(dto.getNauthSearchSum());
        }
        
        if (dto.getNauthSaveDaily() != null) {
            member.setNauthSaveDaily(dto.getNauthSaveDaily());
        }
        if (dto.getNauthSaveDetail() != null) {
            member.setNauthSaveDetail(dto.getNauthSaveDetail());
        }
        if (dto.getNauthSaveSum() != null) {
            member.setNauthSaveSum(dto.getNauthSaveSum());
        }

        // 권한 메뉴 관리
        if (dto.getAuthMember() != null) {
            member.setAuthMember(dto.getAuthMember());
        }
        if (dto.getAuthServer() != null) {
            member.setAuthServer(dto.getAuthServer());
        }
        if (dto.getAuthAdvertiser() != null) {
            member.setAuthAdvertiser(dto.getAuthAdvertiser());
        }
        if (dto.getAuthMediacompany() != null) {
            member.setAuthMediacompany(dto.getAuthMediacompany());
        }


        if (dto.getAuthAnswerMsn() != null) {
            member.setAuthAnswerMsn(dto.getAuthAnswerMsn());
        }
        if (dto.getAuthCurAnswer() != null) {
            member.setAuthCurAnswer(dto.getAuthCurAnswer());
        }
        if (dto.getAuthAnswerUpload() != null) {
            member.setAuthAnswerUpload(dto.getAuthAnswerUpload());
        }

        if (dto.getAuthSearchMsn() != null) {
            member.setAuthSearchMsn(dto.getAuthSearchMsn());
        }
        if (dto.getAuthCurSearch() != null) {
            member.setAuthCurSearch(dto.getAuthCurSearch());
        }
        if (dto.getAuthSearchUpload() != null) {
            member.setAuthSearchUpload(dto.getAuthSearchUpload());
        }

        if (dto.getAuthSaveMsn() != null) {
            member.setAuthSaveMsn(dto.getAuthSaveMsn());
        }
        if (dto.getAuthCurSave() != null) {
            member.setAuthCurSave(dto.getAuthCurSave());
        }
        if (dto.getAuthSaveUpload() != null) {
            member.setAuthSaveUpload(dto.getAuthSaveUpload());
        }

        if (dto.getAuthMsnCS() != null) {
            member.setAuthMsnCS(dto.getAuthMsnCS());
        }

        if (dto.getAuthAnswerDaily() != null) {
            member.setAuthAnswerDaily(dto.getAuthAnswerDaily());
        }
        if (dto.getAuthAnswerDetail() != null) {
            member.setAuthAnswerDetail(dto.getAuthAnswerDetail());
        }
        if (dto.getAuthAnswerSum() != null) {
            member.setAuthAnswerSum(dto.getAuthAnswerSum());
        }

        if (dto.getAuthSaveDaily() != null) {
            member.setAuthSaveDaily(dto.getAuthSaveDaily());
        }
        if (dto.getAuthSaveDetail() != null) {
            member.setAuthSaveDetail(dto.getAuthSaveDetail());
        }
        if (dto.getAuthSaveSum() != null) {
            member.setAuthSaveSum(dto.getAuthSaveSum());
        }

        if (dto.getAuthSearchDaily() != null) {
            member.setAuthSearchDaily(dto.getAuthSearchDaily());
        }
        if (dto.getAuthSearchDetail() != null) {
            member.setAuthSearchDetail(dto.getAuthSearchDetail());
        }
        if (dto.getAuthSearchSum() != null) {
            member.setAuthSearchSum(dto.getAuthSearchSum());
        }

        memberRepository.save(member);
        return member;
    }


    public Member delete(int idx) {
        Member target = memberRepository.findByIdx(idx);
        if(target == null)
            return null;
        memberRepository.delete(target);
        return target;
    }//삭제


    public List<Member> getMembers() {
        return memberRepository.findAll();
    }// 모든 Member 데이터 읽기

    public void updateLastLoginTime(String id) {  //최종 로그인 시간 업데이트
        Member member = memberRepository.findById(id);

        if (member != null) {
            LocalDateTime currentDateTime = LocalDateTime.now();
            ZoneId zoneId = ZoneId.of("Asia/Seoul");
            ZonedDateTime zonedDateTime = currentDateTime.atZone(zoneId).plusHours(9);;// LocalDateTime을 ZonedDateTime으로 변환
            member.setLastLoginAt(zonedDateTime); // 현재 시간으로 설정
            memberRepository.save(member); // 업데이트
        }
    }


    public Member getMember(int idx) {
        return memberRepository.findByIdx(idx);
    }

    public List<Member> searchMember(MemberSearchDto dto) {
        List<Member> target_date=null;
        List<Member> target_is_active=null;
        List<Member> target_name=null;
        List<Member> target_id=null;
        List<Member> result=null;


        if(dto.getStartDate() != null || dto.getEndDate() != null){
            if(dto.getStartDate() != null){

                if(dto.getEndDate() == null){
                    ZoneId zoneId = ZoneId.of("Asia/Seoul");
                    ZonedDateTime start_time = dto.getStartDate().atStartOfDay(zoneId);
                    target_date = memberRepository.findByStartDate(start_time);
                }else{
                    ZoneId zoneId = ZoneId.of("Asia/Seoul");
                    ZonedDateTime start_time = dto.getStartDate().atStartOfDay(zoneId);
                    ZonedDateTime end_time = dto.getEndDate().atStartOfDay(zoneId);

                    target_date = memberRepository.findByBothDate(start_time,end_time);
                }

            }
            else {
                ZoneId zoneId = ZoneId.of("Asia/Seoul");
                ZonedDateTime end_time = dto.getEndDate().atStartOfDay(zoneId);

                target_date = memberRepository.findByEndDate(end_time);
            }

        }
        if(dto.getIsActive() != null){
            target_is_active = memberRepository.findByIsActive(dto.getIsActive());
        }
        if(dto.getId()!=null && !dto.getId().isEmpty()){
            target_id = memberRepository.findById_search(dto.getId());
        }

        if(dto.getName()!=null &&!dto.getName().isEmpty()){
            target_name = memberRepository.findByName_search(dto.getName());
        }

        if(target_date!=null) {
            result = new ArrayList<>(target_date);
            if(target_is_active!=null)
                result.retainAll(target_is_active);
            if(target_name!=null)
                result.retainAll(target_name);
            if(target_id!=null)
                result.retainAll(target_id);
        }
        else if(target_is_active !=null){
            result = new ArrayList<>(target_is_active);
            if(target_name !=null)
                result.retainAll(target_name);
            if(target_id != null)
                result.retainAll(target_id);
            
        } else if (target_name != null) {
            result = new ArrayList<>(target_name);
        }
        else if(target_id != null){
            result = new ArrayList<>(target_id);
        }
        if(result ==null)
            result = new ArrayList<>();
        return result;
    }
}
