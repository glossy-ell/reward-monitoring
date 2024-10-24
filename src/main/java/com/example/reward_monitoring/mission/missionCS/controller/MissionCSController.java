package com.example.reward_monitoring.mission.missionCS.controller;


import com.example.reward_monitoring.general.advertiser.entity.Advertiser;
import com.example.reward_monitoring.general.member.entity.Member;
import com.example.reward_monitoring.general.member.repository.MemberRepository;
import com.example.reward_monitoring.mission.answerMsn.entity.AnswerMsn;
import com.example.reward_monitoring.mission.missionCS.dto.MissionCSSearchDto;
import com.example.reward_monitoring.mission.missionCS.entity.MissionCS;
import com.example.reward_monitoring.mission.missionCS.repository.MissionCSRepository;
import com.example.reward_monitoring.mission.missionCS.service.MissionCSService;
import com.example.reward_monitoring.mission.saveMsn.dto.SaveMsnSearchDto;
import com.example.reward_monitoring.mission.saveMsn.entity.SaveMsn;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@Controller
@Tag(name = "MissionCS", description = "미션CS관리 API")
@RequestMapping(value = "/Mission")
public class MissionCSController {
    @Autowired
    private MissionCSRepository missionCSRepository;
    @Autowired
    private MissionCSService missionCSService;
    @Autowired
    private MemberRepository memberRepository;

    @GetMapping("MissionCS/{idx}")  //미션 검색 (idx)
    public ResponseEntity<MissionCS> getMissionCS(@PathVariable int idx){
        MissionCS target = missionCSService.getMissionCS(idx);
        return (target != null) ?
                ResponseEntity.status(HttpStatus.OK).body(target):
                ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/missionCS/missionCSs")  //전체 광고주 리스트 반환
    public ResponseEntity<List<MissionCS>> getMissionCSs(){
        return ResponseEntity.status(HttpStatus.OK).body(missionCSService.getMissionCSs());
    }

    @DeleteMapping("missionCS/delete/{idx}")  // DELETE
    public ResponseEntity<String> delete(@PathVariable int idx)throws IOException {

        MissionCS deleted  = missionCSService.delete(idx);
        return (deleted != null) ?
                ResponseEntity.status(HttpStatus.NO_CONTENT).build():
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

    }
    @Operation(summary = "미션CS 검색", description = "조건에 맞는 CS를 검색합니다")
    @PostMapping({"/missionCsList/search","/missionCsList/search/","/missionCsList/search/{pageNumber}"})
    @ResponseBody
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "검색 중 예기치않은 오류발생")
    })
    public Map<String, Object> searchMissionCS(@PathVariable(required = false,value = "pageNumber") Integer pageNumber,HttpSession session, @RequestBody MissionCSSearchDto dto){

        Member sessionMember= (Member) session.getAttribute("member");
        Map<String, Object> response = new HashMap<>();
        if(sessionMember == null){
            response.put("error", "404"); // 멤버가 없는 경우
            return response;
        } // 세션만료

        Member member =memberRepository.findById( sessionMember.getId());
        if (member == null) {
            response.put("error", "403"); // 비권한 사용자인 경우
            return response;
        }//데이터 없음

        if(member.isNauthAnswerMsn()) { // 비권한 활성화시
            response.put("error", "403");
            return response;
        }


        List<MissionCS> result = missionCSService.searchSMissionCS(dto);
        Collections.reverse(result);
        // 페이지 번호가 없으면 기본값 1 사용
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }

        // 한 페이지당 최대 10개 데이터
        int limit = 10;
        int startIndex = (pageNumber - 1) * limit;

        // 전체 리스트의 크기 체크
        List<MissionCS> limitedMissionCS;
        if (startIndex < result.size()) {
            int endIndex = Math.min(startIndex + limit, result.size());
            limitedMissionCS= result.subList(startIndex, endIndex);
        } else {
            limitedMissionCS = new ArrayList<>(); // 페이지 번호가 범위를 벗어난 경우 빈 리스트
        }

        int totalPages = (int) Math.ceil((double) result.size() / limit);
        int startPage = ((pageNumber - 1) / limit) * limit + 1; // 현재 페이지 그룹의 시작 페이지
        int endPage = Math.min(startPage + limit - 1, totalPages); // 현재 페이지 그룹의 끝 페이지

        response.put("missionCSList", limitedMissionCS);
        response.put("currentPage", pageNumber);
        response.put("totalPages", totalPages);
        response.put("startPage", startPage);
        response.put("endPage", endPage);
        return response; // JSON 형태로 반환
    }

    @Operation(summary = "잘못된 URL 캐치 ", description = "검색중 재진입시 ")
    @GetMapping("/missionCsList/search/{pageNumber}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
    })
    public String  searchMissionCS_return(@PathVariable(required = false,value = "pageNumber") Integer pageNumber,HttpSession session,Model model){
        Member sessionMember = (Member) session.getAttribute("member");
        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료
        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }
        List<MissionCS> MissionCSList = missionCSService.getMissionCSs();

        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        // 한 페이지당 최대 10개 데이터
        int limit = 10;
        int startIndex = (pageNumber - 1) * limit;


        // 전체 리스트의 크기 체크
        List<MissionCS> limitedMissionCSList;
        if (startIndex < MissionCSList.size()) {
            int endIndex = Math.min(startIndex + limit, MissionCSList.size());
            limitedMissionCSList = MissionCSList.subList(startIndex, endIndex);
        } else {
            limitedMissionCSList = new ArrayList<>(); // 페이지 번호가 범위를 벗어난 경우 빈 리스트
        }
        // 전체 페이지 수 계산
        int totalPages = (int) Math.ceil((double) MissionCSList.size() / limit);
        int startPage = ((pageNumber - 1) / limit) * limit + 1; // 현재 페이지 그룹의 시작 페이지
        int endPage = Math.min(startPage + limit - 1, totalPages); // 현재 페이지 그룹의 끝 페이지


        model.addAttribute("missionCSList", limitedMissionCSList);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "missionCsList";
    }


    @GetMapping({"/missionCsList/{pageNumber}","/missionCsList","/missionCsList"})
    public String missionCSList(@PathVariable(required = false,value = "pageNumber") Integer pageNumber,HttpSession session, Model model){
        Member sessionMember = (Member) session.getAttribute("member");
        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료
        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }
        List<MissionCS> MissionCSList = missionCSService.getMissionCSs();

        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        // 한 페이지당 최대 10개 데이터
        int limit = 10;
        int startIndex = (pageNumber - 1) * limit;


        // 전체 리스트의 크기 체크
        List<MissionCS> limitedMissionCSList;
        if (startIndex < MissionCSList.size()) {
            int endIndex = Math.min(startIndex + limit, MissionCSList.size());
            limitedMissionCSList = MissionCSList.subList(startIndex, endIndex);
        } else {
            limitedMissionCSList = new ArrayList<>(); // 페이지 번호가 범위를 벗어난 경우 빈 리스트
        }
        // 전체 페이지 수 계산
        int totalPages = (int) Math.ceil((double) MissionCSList.size() / limit);
        int startPage = ((pageNumber - 1) / limit) * limit + 1; // 현재 페이지 그룹의 시작 페이지
        int endPage = Math.min(startPage + limit - 1, totalPages); // 현재 페이지 그룹의 끝 페이지


        model.addAttribute("missionCSList", limitedMissionCSList);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "missionCsList";
    }
}
