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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        if(dto.getPassword()!=null  && !dto.getPassword().isEmpty())
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
            member.setLastLoginAt(currentDateTime); // 현재 시간으로 설정
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
        boolean changed = false;

        if(dto.getStartDate() != null || dto.getEndDate() != null){
            if(dto.getStartDate() != null){

                if(dto.getEndDate() == null){
                    LocalDateTime start_time = dto.getStartDate().atStartOfDay();
                    target_date = memberRepository.findByStartDate(start_time);
                }else{
                    LocalDateTime start_time = dto.getStartDate().atStartOfDay();
                    LocalDateTime  end_time = dto.getEndDate().atTime(23,59);

                    target_date = memberRepository.findByBothDate(start_time,end_time);
                }

            }
            else {
                LocalDateTime  end_time = dto.getEndDate().atTime(23,59);
                target_date = memberRepository.findByEndDate(end_time);
            }

        }
        if(dto.getIsActive() != null){
            target_is_active = memberRepository.findByIsActive(dto.getIsActive());
        }
        else{
            target_is_active = memberRepository.findAll();
        }
        if(dto.getId()!=null && !dto.getId().isEmpty()){
            target_id = memberRepository.findById_search(dto.getId());
        }

        if(dto.getName()!=null &&!dto.getName().isEmpty()){
            target_name = memberRepository.findByName_search(dto.getName());
        }

        result = new ArrayList<>(memberRepository.findAll());

        if(target_date !=null){
            Set<Integer> idxSet = target_date.stream().map(Member::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(Member -> idxSet.contains(Member.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }

        if(target_is_active !=null){
            Set<Integer> idxSet = target_is_active.stream().map(Member::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(Member -> idxSet.contains(Member.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }

        if(target_name !=null){
            Set<Integer> idxSet = target_name.stream().map(Member::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(Member-> idxSet.contains(Member.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }

        if(target_id !=null){
            Set<Integer> idxSet = target_id.stream().map(Member::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(Member -> idxSet.contains(Member.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }

        if(!changed)
            result = new ArrayList<>();
        return result;
    }
}
