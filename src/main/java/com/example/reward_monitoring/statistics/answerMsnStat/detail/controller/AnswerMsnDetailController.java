package com.example.reward_monitoring.statistics.answerMsnStat.detail.controller;



import com.example.reward_monitoring.general.advertiser.entity.Advertiser;
import com.example.reward_monitoring.general.advertiser.service.AdvertiserService;
import com.example.reward_monitoring.general.mediaCompany.entity.MediaCompany;
import com.example.reward_monitoring.general.mediaCompany.service.MediaCompanyService;
import com.example.reward_monitoring.general.member.entity.Member;
import com.example.reward_monitoring.general.member.repository.MemberRepository;
import com.example.reward_monitoring.mission.missionCS.entity.MissionCS;
import com.example.reward_monitoring.statistics.answerMsnStat.detail.dto.AnswerMsnDetailSearchDto;
import com.example.reward_monitoring.statistics.answerMsnStat.detail.entity.AnswerMsnDetailsStat;
import com.example.reward_monitoring.statistics.answerMsnStat.detail.service.AnswerMsnDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/Statistics/statQuiz")
public class AnswerMsnDetailController {

    @Autowired
    AnswerMsnDetailService answerMsnDetailService;

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    AdvertiserService advertiserService;
    @Autowired
    MediaCompanyService mediaCompanyService;

    @Operation(summary = "정답미션 검색", description = "조건에 맞는 정답미션 디테일 통계을 검색합니다")
    @PostMapping("/search")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
            @ApiResponse(responseCode = "500", description = "검색 중 예기치않은 오류발생")
    })
    public ResponseEntity<List<AnswerMsnDetailsStat>> searchAnswerMsn(HttpSession session, @RequestBody AnswerMsnDetailSearchDto dto){
        List<AnswerMsnDetailsStat> result = answerMsnDetailService.searchAnswerMsnDetail(dto);
        return (result != null) ?
                ResponseEntity.status(HttpStatus.OK).body(result): // 일치하는 결과가 없을경우 빈 리스트 반환
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }


    @GetMapping({"/{pageNumber}","/",""})
    public String statQuiz(@PathVariable(required = false,value = "pageNumber") Integer pageNumber, HttpSession session, Model model){
        Member sessionMember = (Member) session.getAttribute("member");
        List<Advertiser> advertisers = advertiserService.getAdvertisers();
        List<MediaCompany> mediaCompanys = mediaCompanyService.getMediaCompanys();
        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료
        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }
        List<AnswerMsnDetailsStat> answerMsnDetailsStats = answerMsnDetailService.getAnswerMsnsDetails();
        Collections.reverse(answerMsnDetailsStats);

        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        // 한 페이지당 최대 10개 데이터
        int limit = 10;
        int startIndex = (pageNumber - 1) * limit;


        // 전체 리스트의 크기 체크
        List<AnswerMsnDetailsStat> limitedAnswerMsnDetailsStats;
        if (startIndex < answerMsnDetailsStats.size()) {
            int endIndex = Math.min(startIndex + limit, answerMsnDetailsStats.size());
            limitedAnswerMsnDetailsStats = answerMsnDetailsStats.subList(startIndex, endIndex);
        } else {
            limitedAnswerMsnDetailsStats = new ArrayList<>(); // 페이지 번호가 범위를 벗어난 경우 빈 리스트
        }
        // 전체 페이지 수 계산
        int totalPages = (int) Math.ceil((double) answerMsnDetailsStats.size() / limit);
        int startPage = ((pageNumber - 1) / limit) * limit + 1; // 현재 페이지 그룹의 시작 페이지
        int endPage = Math.min(startPage + limit - 1, totalPages); // 현재 페이지 그룹의 끝 페이지

        model.addAttribute("answerMsnDetailsStats", limitedAnswerMsnDetailsStats);
        model.addAttribute("advertisers ", advertisers);
        model.addAttribute("mediaCompanys", mediaCompanys);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "statQuiz";
    }
}
