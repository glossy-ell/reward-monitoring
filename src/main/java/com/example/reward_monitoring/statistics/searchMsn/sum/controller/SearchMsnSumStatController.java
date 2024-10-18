package com.example.reward_monitoring.statistics.searchMsn.sum.controller;


import com.example.reward_monitoring.general.advertiser.entity.Advertiser;
import com.example.reward_monitoring.general.advertiser.service.AdvertiserService;
import com.example.reward_monitoring.general.mediaCompany.entity.MediaCompany;
import com.example.reward_monitoring.general.mediaCompany.service.MediaCompanyService;
import com.example.reward_monitoring.general.member.entity.Member;
import com.example.reward_monitoring.general.member.repository.MemberRepository;
import com.example.reward_monitoring.general.userServer.entity.Server;
import com.example.reward_monitoring.general.userServer.service.ServerService;
import com.example.reward_monitoring.statistics.answerMsnStat.sum.entity.AnswerMsnSumStat;
import com.example.reward_monitoring.statistics.saveMsn.sum.entity.SaveMsnSumStat;
import com.example.reward_monitoring.statistics.searchMsn.sum.dto.SearchMsnSumStatSearchDto;
import com.example.reward_monitoring.statistics.searchMsn.sum.entity.SearchMsnSumStat;
import com.example.reward_monitoring.statistics.searchMsn.sum.service.SearchMsnSumStatService;
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
@RequestMapping("/Statistics/statSumSearch")
public class SearchMsnSumStatController {

    @Autowired
    private SearchMsnSumStatService searchMsnSumStatService;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    AdvertiserService advertiserService;
    @Autowired
    MediaCompanyService mediaCompanyService;
    @Autowired
    ServerService serverService;

    @GetMapping("/SaveMsnSumStats")  //전체 광고주 리스트 반환
    public ResponseEntity<List<SearchMsnSumStat>> getSaveMsnSumStats(){
        return ResponseEntity.status(HttpStatus.OK).body(searchMsnSumStatService.getSearchMsnSumStats());
    }

    @Operation(summary = "검색미션 검색", description = "조건에 맞는 검색미션을 검색합니다")
    @PostMapping("/search")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
            @ApiResponse(responseCode = "500", description = "검색 중 예기치않은 오류발생")
    })
    public ResponseEntity<List<SearchMsnSumStat>> searchSaveMsnSum(@RequestBody SearchMsnSumStatSearchDto dto){
        List<SearchMsnSumStat> result = searchMsnSumStatService.searchSearchMsnSum(dto);
        return (result != null) ?
                ResponseEntity.status(HttpStatus.OK).body(result): // 일치하는 결과가 없을경우 빈 리스트 반환
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @RequestMapping({"/",""})
    public String statSumSearch(HttpSession session, Model model){
        Member sessionMember = (Member) session.getAttribute("member");
        List<Advertiser> advertisers = advertiserService.getAdvertisers();
        List<MediaCompany> mediaCompanys = mediaCompanyService.getMediaCompanys();
        List<Server> servers = serverService.getServers();
        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료
        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }

        List<SearchMsnSumStat> searchMsnSumStats = searchMsnSumStatService.getSearchMsnSumStats();
        Collections.reverse(searchMsnSumStats);
        if (searchMsnSumStats.size() > 30) {
            searchMsnSumStats = searchMsnSumStats.subList(0, 30);
        }

        int totalLandingCount = searchMsnSumStats.stream().mapToInt(SearchMsnSumStat::getLandingCount).sum();  // 랜딩카운트 합
        int totalPartCount =  searchMsnSumStats.stream().mapToInt(SearchMsnSumStat::getPartCount).sum();  // 참여카운트 합
        model.addAttribute("searchMsnSumStats", searchMsnSumStats);
        model.addAttribute("servers", servers);
        model.addAttribute("advertisers", advertisers);
        model.addAttribute("mediaCompanys", mediaCompanys);
        model.addAttribute("totalLandingCount",totalLandingCount);
        model.addAttribute("totalPartCount",totalPartCount);
        return "statSumSearch";
    }
}
