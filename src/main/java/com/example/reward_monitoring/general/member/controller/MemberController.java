package com.example.reward_monitoring.general.member.controller;


import com.example.reward_monitoring.general.member.dto.MemberEditDto;
import com.example.reward_monitoring.general.member.dto.MemberReadDto;
import com.example.reward_monitoring.general.member.dto.MemberSearchDto;
import com.example.reward_monitoring.general.member.dto.editMyDto;
import com.example.reward_monitoring.general.member.entity.Member;
import com.example.reward_monitoring.general.member.model.Auth;
import com.example.reward_monitoring.general.member.repository.MemberRepository;
import com.example.reward_monitoring.general.member.service.MemberService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;



@Controller
@Tag(name = "Member", description = "관리자 api ")
@RequestMapping("/Admin")
public class MemberController {


    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MemberService memberService;




    @Operation(summary = "본인 회원정보수정", description = " 본인 회원정보를 수정합니다.")
    @PostMapping("/Profile/myProfileWrite")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원정보 수정 성공"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "404", description = "조회 실패(세션은 있지만 세션정보가 DB에 없음)"),
            @ApiResponse(responseCode = "500", description = "회원정보 수정 실패")
    })
    public ResponseEntity<Void> editInfo(HttpSession session, editMyDto dto){
        Member sessionMember= (Member) session.getAttribute("member");

        if(sessionMember== null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Member member =memberRepository.findById( sessionMember.getId());
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }//데이터 없음

        if(dto.getPassword()!=null)
            member.setPassword(passwordEncoder.encode(dto.getPassword()));
        if(dto.getPhoneNum()!=null)
            member.setPhone(dto.getPhoneNum());
        if(dto.getCtryCode()!=null)
            member.setCtryCode(dto.getCtryCode());

        memberRepository.save(member);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @Operation(summary = "관리자 정보 수정", description = "관리자 정보를 수정합니다")
    @Parameter(name = "idx", description = "수정할 관리자의 IDX")
    @PostMapping("/member/edit/{idx}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 수정됨"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "404", description = "회원정보 조회 실패")

    })
    public ResponseEntity<Member> edit(HttpSession session,@PathVariable int idx,@RequestBody MemberEditDto dto,HttpServletResponse response){
        Member sessionMember= (Member) session.getAttribute("member");
        if(sessionMember == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } // 세션만료

        Member member =memberRepository.findById( sessionMember.getId());
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }//데이터 없음

        if(member.isNauthMember()) // 비권한 활성화시
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        if(member.getAuthMember()== Auth.READ) // 읽기 권한만 존재할경우
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        Member edited = memberService.edit(idx,dto);
        if(edited == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        ZonedDateTime editedAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        edited.setEditedAt(editedAt); //수정시간 설정
        return ResponseEntity.status(HttpStatus.OK).body(edited);
    }
    
    @Operation(summary = "관리자 가입", description = "관리자 정보를 생성합니다")
    @PostMapping("/member/join")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "성공적으로 수정됨"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류로 계정생성이 되지않음")
    })
    public ResponseEntity<Void> join(HttpSession session,@RequestBody MemberReadDto dto){

        Member sessionMember= (Member) session.getAttribute("member");
        if(sessionMember == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } // 세션만료

        Member member =memberRepository.findById( sessionMember.getId());
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }//데이터 없음

        if(member.isNauthMember()) // 비권한 활성화시
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        if(member.getAuthMember()== Auth.READ) // 읽기 권한만 존재할경우
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

//테스트 위해 임시 주석 처리
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
            @ApiResponse(responseCode = "204", description = "일치하는 회원을 찾을 수 없음"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음")
    })
    public ResponseEntity<Member> getMember(HttpSession session,@PathVariable int idx){

        Member sessionMember= (Member) session.getAttribute("member");
        if(sessionMember == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } // 세션만료

        Member member =memberRepository.findById( sessionMember.getId());
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }//데이터 없음

        if(member.isNauthMember()) // 비권한 활성화시
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();


        Member target = memberService.getMember(idx);
        return (target != null) ?
                ResponseEntity.status(HttpStatus.OK).body(target):
                ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "전체 관리자 정보 요청", description = "전체 관리자 정보를 반환합니다")
    @GetMapping("/member/members")  //멤버 리스트 반환
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음")
    })
    public ResponseEntity<List<Member>> getMembers(HttpSession session){

        Member sessionMember= (Member) session.getAttribute("member");
        if(sessionMember == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } // 세션만료
        Member member =memberRepository.findById( sessionMember.getId());
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }//데이터 없음

        if(member.isNauthMember()) // 비권한 활성화시
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();


        return ResponseEntity.status(HttpStatus.OK).body(memberService.getMembers());
    }

    @Operation(summary = "관리자 삭제", description = "IDX와 일치하는 단일 관리자정보를 삭제합니다")
    @Parameter(name = "idx", description = "관리자정보 IDX")
    @DeleteMapping("/member/delete/{idx}")  // 회원탈퇴
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 완료 "),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "404", description = "일치하는 정보가 없음"),

    })
    public ResponseEntity<Void> delete(HttpSession session,@PathVariable int idx)throws IOException{

        Member sessionMember= (Member) session.getAttribute("member");
        if(sessionMember == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } // 세션만료

        Member member =memberRepository.findById( sessionMember.getId());
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }//데이터 없음

        if(member.isNauthMember()) // 비권한 활성화시
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        if(member.getAuthMember()== Auth.READ) // 읽기 권한만 존재할경우
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        Member deleted  = memberService.delete(idx);
        return (deleted != null) ?
                ResponseEntity.status(HttpStatus.NO_CONTENT).build():
                ResponseEntity.status(HttpStatus.NOT_FOUND).build();

    }


    @Operation(summary = "관리자 검색", description = "조건에 맞는 관리자를 검색합니다")
    @PostMapping("/member/search")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "검색 중 예기치않은 오류발생")
    })
    public ResponseEntity<List<Member>> searchMember(HttpSession session, @RequestBody MemberSearchDto dto){

        Member sessionMember= (Member) session.getAttribute("member");
        if(sessionMember == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } // 세션만료

        Member member =memberRepository.findById( sessionMember.getId());
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }//데이터 없음
        if(member.isNauthMember()) // 비권한 활성화시
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        List<Member> result = memberService.searchMember(dto);
        return (result != null) ?
                ResponseEntity.status(HttpStatus.OK).body(result): // 일치하는 결과가 없을경우 빈 리스트 반환
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }



    @GetMapping({"/adminList","/",""})
    public String myProfile(HttpSession session, Model model) {
        Member sessionMember = (Member) session.getAttribute("member");
        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료
        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }
        List<Member> members = memberService.getMembers();
        model.addAttribute("members",members);
        return "adminList";
    }

    @GetMapping("/adminWrite")
    public String adminWrite(HttpSession session){
        Member sessionMember = (Member) session.getAttribute("member");
        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료
        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }

        return "adminWrite";
    }

    @GetMapping("/adminWrite/{idx}")
    public String adminEdit(HttpSession session,Model model,@PathVariable int idx){

        Member sessionMember = (Member) session.getAttribute("member");
        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료
        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }

        Member foundMember = memberService.getMember(idx);
        if(foundMember==null)
            return "error/404";
        model.addAttribute("member", foundMember);
        return "adminWrite";
    }


}
