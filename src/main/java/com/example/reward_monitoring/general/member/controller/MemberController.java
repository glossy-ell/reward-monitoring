package com.example.reward_monitoring.general.member.controller;


import com.example.reward_monitoring.general.member.dto.MemberEditDto;
import com.example.reward_monitoring.general.member.dto.MemberReadDto;
import com.example.reward_monitoring.general.member.dto.MemberSearchDto;
import com.example.reward_monitoring.general.member.entity.Member;
import com.example.reward_monitoring.general.member.repository.MemberRepository;
import com.example.reward_monitoring.general.member.service.MemberService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;



@Controller
@Tag(name = "Member", description = "관리자 api ")
public class MemberController {


    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MemberService memberService;



    @Operation(summary = "관리자 정보 수정", description = "관리자 정보를 수정합니다")
    @Parameter(name = "idx", description = "수정할 관리자의 IDX")
    @PostMapping("/member/edit/{idx}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 수정됨"),
            @ApiResponse(responseCode = "500", description = "일치하는 회원을 찾을 수 없음")
    })
    public ResponseEntity<Member> edit(@PathVariable int idx,@RequestBody MemberEditDto dto,HttpServletResponse response){

        Member member = memberService.edit(idx,dto);
        if(member == null){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        ZonedDateTime editedAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        member.setEditedAt(editedAt); //수정시간 설정
        return ResponseEntity.status(HttpStatus.OK).body(member);
    }
    
    @Operation(summary = "관리자 가입", description = "관리자 정보를 생성합니다")
    @PostMapping("/member/join")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "성공적으로 수정됨"),
            @ApiResponse(responseCode = "500", description = "서버 오류로 계정생성이 되지않음")
    })
    public ResponseEntity<Void> join(@RequestBody MemberReadDto dto){
        Member created = memberService.join(dto);
        if(created == null){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        memberRepository.save(created);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    
    
    @Operation(summary = "관리자 정보 요청", description = "IDX와 일치하는 단일 관리자정보를 반환합니다")
    @Parameter(name = "idx", description = "관리자 IDX")
    @GetMapping("/member/{idx}")  //멤버검색(idx)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "관리자 정보 검색 완료 "),
            @ApiResponse(responseCode = "204", description = "일치하는 회원을 찾을 수 없음")
    })
    public ResponseEntity<Member> getMember(@PathVariable int idx){
        Member target = memberService.getMember(idx);
        return (target != null) ?
                ResponseEntity.status(HttpStatus.OK).body(target):
                ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "전체 관리자 정보 요청", description = "전체 관리자 정보를 반환합니다")
    @GetMapping("/member/members")  //멤버 리스트 반환
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료"),
    })
    public ResponseEntity<List<Member>> getMembers(){
        return ResponseEntity.status(HttpStatus.OK).body(memberService.getMembers());
    }

    @Operation(summary = "관리자 삭제", description = "IDX와 일치하는 단일 관리자정보를 삭제합니다")
    @Parameter(name = "idx", description = "관리자정보 IDX")
    @DeleteMapping("/member/delete/{idx}")  // 회원탈퇴
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 완료 "),
            @ApiResponse(responseCode = "400", description = "일치하는 정보가 없음")
    })
    public ResponseEntity<Void> delete(@PathVariable int idx)throws IOException{

        Member deleted  = memberService.delete(idx);
        return (deleted != null) ?
                ResponseEntity.status(HttpStatus.NO_CONTENT).build():
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

    }


    @Operation(summary = "관리자 검색", description = "조건에 맞는 관리자를 검색합니다")
    @PostMapping("/member/search")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
            @ApiResponse(responseCode = "500", description = "검색 중 예기치않은 오류발생")
    })
    public ResponseEntity<List<Member>> searchMember(@RequestBody MemberSearchDto dto){
        List<Member> result = memberService.searchMember(dto);
        return (result != null) ?
                ResponseEntity.status(HttpStatus.OK).body(result):
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }


}
