package com.example.reward_monitoring.general.mediaCompany.controller;

import com.example.reward_monitoring.general.mediaCompany.dto.MediaCompanyEditDto;
import com.example.reward_monitoring.general.mediaCompany.dto.MediaCompanyReadDto;
import com.example.reward_monitoring.general.mediaCompany.dto.MediaCompanySearchDto;
import com.example.reward_monitoring.general.mediaCompany.entity.MediaCompany;
import com.example.reward_monitoring.general.mediaCompany.repository.MediaCompanyRepository;
import com.example.reward_monitoring.general.mediaCompany.service.MediaCompanyService;


import com.example.reward_monitoring.general.member.entity.Member;
import com.example.reward_monitoring.general.member.model.Auth;
import com.example.reward_monitoring.general.member.repository.MemberRepository;
import com.example.reward_monitoring.general.userServer.entity.Server;
import com.example.reward_monitoring.general.userServer.service.ServerService;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Tag(name = "MediaCompany", description = "매체사 API")
@RequestMapping("/Affiliate")
@Slf4j
public class MediaCompanyController {
    @Autowired
    private MediaCompanyRepository mediaCompanyRepository;

    @Autowired
    private MediaCompanyService mediaCompanyService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ServerService serverService;

    @Operation(summary = "매체사 정보 수정", description = "매체사 정보를 수정합니다")
    @Parameter(name = "idx", description = "수정할 매체사의 IDX")
    @PostMapping("/edit/{idx}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 수정됨"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "404", description = "일치하는 회원을 찾을 수 없음")
    })
    public ResponseEntity<MediaCompany> edit(HttpSession session, @PathVariable("idx") int idx, @RequestBody MediaCompanyEditDto dto, HttpServletResponse response){

        Member sessionMember= (Member) session.getAttribute("member");
        if(sessionMember == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } // 세션만료
        Member member =memberRepository.findById( sessionMember.getId());
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }//데이터 없음
        if(member.getAuthMediacompany()== Auth.READ) // 읽기 권한만 존재할경우
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();


        MediaCompany mediaCompany = mediaCompanyService.edit(idx,dto);
        if(mediaCompany == null){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(mediaCompany);

    }
    @Operation(summary = "관리자 가입", description = "관리자 정보를 생성합니다")
    @PostMapping("/add")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 수정됨"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류로 계정생성이 되지않음")
    })
    public ResponseEntity<Void> add(HttpSession session,@RequestBody MediaCompanyReadDto dto){

        Member sessionMember= (Member) session.getAttribute("member");
        if(sessionMember == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } // 세션만료
        Member member =memberRepository.findById( sessionMember.getId());
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }//데이터 없음
        if(member.getAuthMediacompany()== Auth.READ) // 읽기 권한만 존재할경우
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();


        MediaCompany created = mediaCompanyService.add(dto);
        if(created == null){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        mediaCompanyRepository.save(created);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @Operation(summary = "매체사 정보 요청", description = "IDX와 일치하는 단일 매체사정보를 반환합니다")
    @Parameter(name = "idx", description = "관리자 IDX")
    @GetMapping("/idx/{idx}")  //idx 검색
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "관리자 정보 검색 완료 "),
            @ApiResponse(responseCode = "204", description = "일치하는 회원을 찾을 수 없음"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨")
    })
    public ResponseEntity<MediaCompany> getId(HttpSession session,@PathVariable("idx") int idx){
        Member sessionMember= (Member)session.getAttribute("member");
        if(sessionMember == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } // 세션만료
        Member member =memberRepository.findById( sessionMember.getId());
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }//데이터 없음


        MediaCompany target = mediaCompanyService.getMediaCompany(idx);
        return (target != null) ?
                ResponseEntity.status(HttpStatus.OK).body(target):
                ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "전체 매체사 정보 요청", description = "전체 매체사 정보를 반환합니다")
    @GetMapping("/Affiliates")  //매체사 리스트 반환
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 완료 "),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "500", description = "검색중 오류 발생")
    })
    public ResponseEntity<List<MediaCompany>> getMediaCompanys(HttpSession session){

        Member sessionMember= (Member)session.getAttribute("member");
        if(sessionMember == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } // 세션만료
        Member member =memberRepository.findById( sessionMember.getId());
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }//데이터 없음

        return ResponseEntity.status(HttpStatus.OK).body(mediaCompanyService.getMediaCompanys());
    }
    @Operation(summary = "매체사 삭제", description = "IDX와 일치하는 단일 매체사정보를 삭제합니다")
    @Parameter(name = "idx", description = "매체사정보 IDX")
    @DeleteMapping("/delete/{idx}")  // 회원탈퇴
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 완료 "),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "삭제중 오류 발생")
    })
    public ResponseEntity<String> delete(HttpSession session, @PathVariable("idx") int idx)throws IOException {
        Member sessionMember= (Member) session.getAttribute("member");
        if(sessionMember == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } // 세션만료
        Member member =memberRepository.findById( sessionMember.getId());
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }//데이터 없음
        if(member.getAuthMediacompany()== Auth.READ) // 읽기 권한만 존재할경우
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();


        MediaCompany deleted  = mediaCompanyService.delete(idx);
        return (deleted != null) ?
                ResponseEntity.status(HttpStatus.NO_CONTENT).build():
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }


    @Operation(summary = "매체사 검색", description = "조건에 맞는 매체사를 검색합니다")
    @PostMapping({"/search","/search/{pageNumber}"})
    @ResponseBody
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "500", description = "검색 중 예기치않은 오류발생")
    })
    public Map<String, Object> searchMediaCompany(@PathVariable(required = false,value = "pageNumber") Integer pageNumber,HttpSession session, @RequestBody MediaCompanySearchDto dto){
        Member sessionMember= (Member) session.getAttribute("member");
        Map<String, Object> response = new HashMap<>();
        if(sessionMember == null) {
            response.put("error", "404"); // 멤버가 없는 경우
            return response; // JSON 형태로 반환
        }
        Member member =memberRepository.findById( sessionMember.getId());
        if (member == null) {
            response.put("error", "404"); // 비권한 사용자인 경우
            return response; // JSON 형태로 반환
        }
        if(member.isNauthMember()){
            response.put("error", "403");
            return response;
        }


        List<MediaCompany> result = mediaCompanyService.searchMediaCompany(dto);

        log.info(result.toString());
        // 페이지 번호가 없으면 기본값 1 사용
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }

        // 한 페이지당 최대 15개 데이터
        int limit = 15;
        int startIndex = (pageNumber - 1) * limit;
        // 전체 리스트의 크기 체크
        List<MediaCompany> limitedMediaCompanys;
        if (startIndex < result.size()) {
            int endIndex = Math.min(startIndex + limit, result.size());
            limitedMediaCompanys = result.subList(startIndex, endIndex);
        } else {
            limitedMediaCompanys = new ArrayList<>(); // 페이지 번호가 범위를 벗어난 경우 빈 리스트
        }

        int totalPages = (int) Math.ceil((double) result.size() / limit);
        response.put("affiliates", limitedMediaCompanys);  // limitedMembers 리스트
        response.put("currentPage", pageNumber);  // 현재 페이지 번호
        response.put("totalPages", totalPages);    // 전체 페이지 수
        return response; // JSON 형태로 반환

    }

    @GetMapping({"/affiliateList/{pageNumber}","/affiliateList","/",""})
    public String affiliateList(@PathVariable(required = false,value = "pageNumber") Integer pageNumber ,HttpSession session, Model model) {
        Member sessionMember = (Member) session.getAttribute("member");
        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료
        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }

        List<MediaCompany> mediaCompanyList = mediaCompanyService.getMediaCompanys();
        // 페이지 번호가 없으면 기본값 1 사용
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        // 한 페이지당 최대 15개 데이터
        int limit = 15;
        int startIndex = (pageNumber - 1) * limit;

        // 전체 리스트의 크기 체크
        List<MediaCompany> limitedMediaCompanys;
        if (startIndex < mediaCompanyList.size()) {
            int endIndex = Math.min(startIndex + limit, mediaCompanyList.size());
            limitedMediaCompanys =  mediaCompanyList.subList(startIndex, endIndex);
        } else {
            limitedMediaCompanys = new ArrayList<>(); // 페이지 번호가 범위를 벗어난 경우 빈 리스트
        }

        int totalPages = (int) Math.ceil((double) mediaCompanyList.size() / limit);
        model.addAttribute("mediaCompanyList", limitedMediaCompanys);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", (int) Math.ceil((double) mediaCompanyList.size() / limit));
        return "affiliateList";
    }

    @GetMapping("/affiliateWrite")
    public String affiliateWrite(HttpSession session,Model model){
        Member sessionMember = (Member) session.getAttribute("member");
        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료
        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }
        List<Server> server = serverService.getServers();

        model.addAttribute("servers",server);
        return "affiliateWrite";
    }

    @GetMapping("/affiliateWrite/{idx}")
    public String affiliateEdit(HttpSession session,@PathVariable(required = false,value = "idx") int idx,Model model){

        Member sessionMember = (Member) session.getAttribute("member");
        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료
        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }

        MediaCompany mediaCompany = mediaCompanyService.getMediaCompany(idx);
        List<Server> server = serverService.getServers();
        if(mediaCompany==null)
            return "error/404";
        model.addAttribute("affiliate", mediaCompany);
        model.addAttribute("servers",server);
        return "affiliateWrite";
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<String> handleSQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException ex) {
        if (ex.getMessage().contains("Duplicate entry")) {
            return new ResponseEntity<>("아이디가 중복되었습니다.", HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>("데이터베이스 오류가 발생했습니다. 다시 시도하거나 관리자에게 문의해주세요", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
