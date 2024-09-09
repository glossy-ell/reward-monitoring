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
        if(dto.getChangedPassword()!=null) {
            String changedPassword = passwordEncoder.encode(dto.getChangedPassword()); //변경할 패스워드
            member.setPassword(changedPassword);
        }
        if(dto.getPhone()!= null) {
            member.setPhone(dto.getPhone());
        }
        if(dto.getIsActive() !=null) {
            boolean bool = dto.getIsActive();
            member.setActive(bool);
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
    }

    public List<Member> getMembers() {
        return memberRepository.findAll();
    }

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
                    ZonedDateTime end_time = dto.getStartDate().atStartOfDay(zoneId);

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
        if(dto.getId()!=null){
            target_id = memberRepository.findById_search(dto.getId());
        }

        if(dto.getName()!=null){
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
        return result;

    }
}
