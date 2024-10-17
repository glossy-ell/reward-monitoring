package com.example.reward_monitoring.statistics.answerMsnStat.sum.controller;


import com.example.reward_monitoring.general.advertiser.entity.Advertiser;
import com.example.reward_monitoring.general.advertiser.service.AdvertiserService;
import com.example.reward_monitoring.general.mediaCompany.entity.MediaCompany;
import com.example.reward_monitoring.general.mediaCompany.service.MediaCompanyService;
import com.example.reward_monitoring.general.member.entity.Member;
import com.example.reward_monitoring.general.member.repository.MemberRepository;
import com.example.reward_monitoring.mission.answerMsn.entity.AnswerMsn;
import com.example.reward_monitoring.statistics.answerMsnStat.sum.Service.AnswerMsnSumStatService;
import com.example.reward_monitoring.statistics.answerMsnStat.sum.dto.AnswerMsnSumStatSearchDto;
import com.example.reward_monitoring.statistics.answerMsnStat.sum.entity.AnswerMsnSumStat;
import com.example.reward_monitoring.statistics.saveMsn.detail.entity.SaveMsnDetailsStat;
import com.example.reward_monitoring.statistics.saveMsn.sum.entity.SaveMsnSumStat;
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
@RequestMapping("/Statistics/statSumQuiz")
public class AnswerMsnSumStatController {

    @Autowired
    private AnswerMsnSumStatService answerMsnSumStatService;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    AdvertiserService advertiserService;
    @Autowired
    MediaCompanyService mediaCompanyService;

    @GetMapping("/AnswerMsnSumStats")  //전체 광고주 리스트 반환
    public ResponseEntity<List<AnswerMsnSumStat>> getAnswerMsnSumStats(){
        return ResponseEntity.status(HttpStatus.OK).body(answerMsnSumStatService.getAnswerMsnSumStats());
    }

    @Operation(summary = "정답미션 검색", description = "조건에 맞는 정답미션을 검색합니다")
    @PostMapping("/search")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
            @ApiResponse(responseCode = "500", description = "검색 중 예기치않은 오류발생")
    })
    public ResponseEntity<List<AnswerMsnSumStat>> searchAnswerMsn(@RequestBody AnswerMsnSumStatSearchDto dto){
        List<AnswerMsnSumStat> result = answerMsnSumStatService.searchAnswerMsnSum(dto);
        return (result != null) ?
                ResponseEntity.status(HttpStatus.OK).body(result): // 일치하는 결과가 없을경우 빈 리스트 반환
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @RequestMapping({"/",""})
    public String statSumQuiz(HttpSession session, Model model){
        Member sessionMember = (Member) session.getAttribute("member");
        List<Advertiser> advertisers = advertiserService.getAdvertisers();
        List<MediaCompany> mediaCompanys = mediaCompanyService.getMediaCompanys();
        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료
        Member member = memberRepository.findById(sessionMember.getId());
        List<AnswerMsnSumStat> answerMsnSumStats = answerMsnSumStatService.getAnswerMsnSumStats();
        Collections.reverse(answerMsnSumStats);
        if (answerMsnSumStats.size() > 30) {
            answerMsnSumStats = answerMsnSumStats.subList(0, 30);
        }

        if (member == null) {
            return "error/404";
        }
        int totalLandingCount = answerMsnSumStats.stream().mapToInt(AnswerMsnSumStat::getLandingCount).sum();  // 랜딩카운트 합
        int totalPartCount =  answerMsnSumStats.stream().mapToInt(AnswerMsnSumStat::getPartCount).sum();  // 참여카운트 합

        model.addAttribute("answerMsnSumStats", answerMsnSumStats);
        model.addAttribute("totalLandingCount",totalLandingCount);
        model.addAttribute("totalPartCount",totalPartCount);
        return "statSumQuiz";
    }
}
