package com.example.reward_monitoring.mission.missionCS.controller;


import com.example.reward_monitoring.general.member.entity.Member;
import com.example.reward_monitoring.general.member.repository.MemberRepository;
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
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@Tag(name = "MissionCS", description = "미션CS관리 API")
@RequestMapping(value = "/MissionCs")
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
    @PostMapping("/search")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "검색 중 예기치않은 오류발생")
    })
    public ResponseEntity<List<MissionCS>> searchMissionCS(HttpSession session, @RequestBody MissionCSSearchDto dto){

        Member sessionMember= (Member)session.getAttribute("member");
        if(sessionMember == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } // 세션만료
        Member member =memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }//데이터 없음


        List<MissionCS> result = missionCSService.searchSMissionCS(dto);
        return (result != null) ?
                ResponseEntity.status(HttpStatus.OK).body(result): // 일치하는 결과가 없을경우 빈 리스트 반환
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @RequestMapping({"missionCsList","/",""})
    public String missionCSList(HttpSession session){
        Member sessionMember = (Member) session.getAttribute("member");
        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료
        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }
        return "missionCsList";
    }
}
