package com.example.reward_monitoring.general.member.controller;


import com.example.reward_monitoring.config.RateLimited;
import com.example.reward_monitoring.general.advertiser.entity.Advertiser;
import com.example.reward_monitoring.general.member.dto.MemberEditDto;
import com.example.reward_monitoring.general.member.dto.MemberReadDto;
import com.example.reward_monitoring.general.member.dto.MemberSearchDto;
import com.example.reward_monitoring.general.member.entity.Member;
import com.example.reward_monitoring.general.member.model.Auth;
import com.example.reward_monitoring.general.member.repository.MemberRepository;
import com.example.reward_monitoring.general.member.service.MemberService;

import com.example.reward_monitoring.mission.answerMsn.entity.AnswerMsn;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
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
import java.util.*;


@Controller
@Tag(name = "Member", description = "관리자 api ")
@RequestMapping("/Admin")
@RateLimited
@Slf4j
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
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "404", description = "회원정보 조회 실패")

    })
    public ResponseEntity<Member> edit(HttpSession session,@PathVariable("idx") int idx,@RequestBody MemberEditDto dto,HttpServletResponse response){
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
    @PostMapping({"/member/search","/member/search/{pageNumber}"})
    @ResponseBody
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "검색 중 예기치않은 오류발생")
    })
    public Map<String, Object> searchMember(@PathVariable(required = false,value = "pageNumber") Integer pageNumber,HttpSession session, @RequestBody MemberSearchDto dto){

        Member sessionMember= (Member) session.getAttribute("member");
        Map<String, Object> response = new HashMap<>();
        if(sessionMember == null){
            response.put("error", "404"); // 멤버가 없는 경우
            return response;
        }

        Member member =memberRepository.findById( sessionMember.getId());
        if (member == null) {
            response.put("error", "403"); // 비권한 사용자인 경우
            return response;
        }
        if(member.isNauthMember()){
            response.put("error", "403");
            return response;
        }


        List<Member> result = memberService.searchMember(dto);
        // 페이지 번호가 없으면 기본값 1 사용
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }

        // 한 페이지당 최대 15개 데이터
        int limit = 15;
        int startIndex = (pageNumber - 1) * limit;

        // 전체 리스트의 크기 체크
        List<Member> limitedMembers;
        if (startIndex < result.size()) {
            int endIndex = Math.min(startIndex + limit, result.size());
            limitedMembers = result.subList(startIndex, endIndex);
        } else {
            limitedMembers = new ArrayList<>(); // 페이지 번호가 범위를 벗어난 경우 빈 리스트
        }

        int totalPages = (int) Math.ceil((double) result.size() / limit);
        int startPage = ((pageNumber - 1) / limit) * limit + 1; // 현재 페이지 그룹의 시작 페이지
        int endPage = Math.min(startPage + limit - 1, totalPages); // 현재 페이지 그룹의 끝 페이지
        response.put("members", limitedMembers);  // limitedMembers 리스트
        response.put("currentPage", pageNumber);  // 현재 페이지 번호
        response.put("totalPages", totalPages);    // 전체 페이지 수
        response.put("startPage",startPage);
        response.put("endPage",endPage);
        return response; // JSON 형태로 반환
    }

    @Operation(summary = "잘못된 URL 캐치 ", description = "검색중 재진입시 ")
    @GetMapping("/member/search/{pageNumber}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
    })
    public String  searchMember_return(@PathVariable(required = false,value = "pageNumber") Integer pageNumber,HttpSession session,Model model){
        Member sessionMember = (Member) session.getAttribute("member");
        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        }
        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }

        List<Member> members = memberService.getMembers();

        // 페이지 번호가 없으면 기본값 1 사용
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }

        // 한 페이지당 최대 15개 데이터
        int limit = 15;
        int startIndex = (pageNumber - 1) * limit;

        // 전체 리스트의 크기 체크
        List<Member> limitedMembers;
        if (startIndex < members.size()) {
            int endIndex = Math.min(startIndex + limit, members.size());
            limitedMembers = members.subList(startIndex, endIndex);
        } else {
            limitedMembers = new ArrayList<>(); // 페이지 번호가 범위를 벗어난 경우 빈 리스트
        }

        int totalPages = (int) Math.ceil((double) members.size() / limit);
        int startPage = ((pageNumber - 1) / limit) * limit + 1; // 현재 페이지 그룹의 시작 페이지
        int endPage = Math.min(startPage + limit - 1, totalPages); // 현재 페이지 그룹의 끝 페이지

        model.addAttribute("members", limitedMembers);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", (int) Math.ceil((double) members.size() / limit));
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "adminList";
    }

    @GetMapping({"/adminList/{pageNumber}", "/adminList", "/", ""})
    public String adminList(@PathVariable(required = false,value = "pageNumber") Integer pageNumber, HttpSession session, Model model) {
        Member sessionMember = (Member) session.getAttribute("member");
        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        }
        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }

        List<Member> members = memberService.getMembers();

        // 페이지 번호가 없으면 기본값 1 사용
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }

        // 한 페이지당 최대 15개 데이터
        int limit = 15;
        int startIndex = (pageNumber - 1) * limit;

        // 전체 리스트의 크기 체크
        List<Member> limitedMembers;
        if (startIndex < members.size()) {
            int endIndex = Math.min(startIndex + limit, members.size());
            limitedMembers = members.subList(startIndex, endIndex);
        } else {
            limitedMembers = new ArrayList<>(); // 페이지 번호가 범위를 벗어난 경우 빈 리스트
        }

        int totalPages = (int) Math.ceil((double) members.size() / limit);
        int startPage = ((pageNumber - 1) / limit) * limit + 1; // 현재 페이지 그룹의 시작 페이지
        int endPage = Math.min(startPage + limit - 1, totalPages); // 현재 페이지 그룹의 끝 페이지

        model.addAttribute("members", limitedMembers);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", (int) Math.ceil((double) members.size() / limit));
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
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

        return "Admin";
    }

    @GetMapping("/adminWrite/{idx}")
    public String adminEdit(HttpSession session,Model model,@PathVariable(required = false,value = "idx") int idx){
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
        model.addAttribute("currentmember",member);
        return "Admin";
    }


}
