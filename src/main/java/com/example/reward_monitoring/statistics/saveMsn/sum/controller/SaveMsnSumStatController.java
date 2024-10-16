package com.example.reward_monitoring.statistics.saveMsn.sum.controller;


import com.example.reward_monitoring.general.member.entity.Member;
import com.example.reward_monitoring.general.member.repository.MemberRepository;
import com.example.reward_monitoring.statistics.answerMsnStat.sum.entity.AnswerMsnSumStat;
import com.example.reward_monitoring.statistics.saveMsn.sum.dto.SaveMsnSumStatSearchDto;
import com.example.reward_monitoring.statistics.saveMsn.sum.entity.SaveMsnSumStat;
import com.example.reward_monitoring.statistics.saveMsn.sum.service.SaveMsnSumStatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/Statistics/statSumSightseeing")
public class SaveMsnSumStatController {

    @Autowired
    private SaveMsnSumStatService saveMsnSumStatService;

    @Autowired
    private MemberRepository memberRepository;
    
    @GetMapping("/SaveMsnSumStats")  //전체 광고주 리스트 반환
    public ResponseEntity<List<SaveMsnSumStat>> getSaveMsnSumStats(){
        return ResponseEntity.status(HttpStatus.OK).body(saveMsnSumStatService.getSaveMsnSumStats());
    }

    @Operation(summary = "저장미션 검색", description = "조건에 맞는 저장미션을 검색합니다")
    @PostMapping("/search")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
            @ApiResponse(responseCode = "500", description = "검색 중 예기치않은 오류발생")
    })
    public ResponseEntity<List<SaveMsnSumStat>> searchSaveMsnSum(@RequestBody SaveMsnSumStatSearchDto dto){
        List<SaveMsnSumStat> result = saveMsnSumStatService.searchSaveMsnSum(dto);
        return (result != null) ?
                ResponseEntity.status(HttpStatus.OK).body(result): // 일치하는 결과가 없을경우 빈 리스트 반환
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @RequestMapping({"/",""})
    public String statSumSightseeing(HttpSession session, Model model){
        Member sessionMember = (Member) session.getAttribute("member");
        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료
        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }
        List<SaveMsnSumStat> saveMsnSumStats = saveMsnSumStatService.getSaveMsnSumStats();
        Collections.reverse(saveMsnSumStats);
        if (saveMsnSumStats.size() > 30) {
            saveMsnSumStats = saveMsnSumStats.subList(0, 30);
        }
        int totalLandingCount = saveMsnSumStats.stream().mapToInt(SaveMsnSumStat::getLandingCount).sum();  // 랜딩카운트 합
        int totalPartCount =  saveMsnSumStats.stream().mapToInt(SaveMsnSumStat::getPartCount).sum();  // 참여카운트 합
        model.addAttribute("saveMsnSumStats", saveMsnSumStats);
        model.addAttribute("totalLandingCount",totalLandingCount);
        model.addAttribute("totalPartCount",totalPartCount);
        return "statSumSightseeing";
    }
}
