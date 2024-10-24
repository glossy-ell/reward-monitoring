package com.example.reward_monitoring.statistics.saveMsn.detail.controller;



import com.example.reward_monitoring.general.advertiser.entity.Advertiser;
import com.example.reward_monitoring.general.advertiser.service.AdvertiserService;
import com.example.reward_monitoring.general.mediaCompany.entity.MediaCompany;
import com.example.reward_monitoring.general.mediaCompany.service.MediaCompanyService;
import com.example.reward_monitoring.general.member.entity.Member;
import com.example.reward_monitoring.general.member.repository.MemberRepository;
import com.example.reward_monitoring.general.userServer.entity.Server;
import com.example.reward_monitoring.general.userServer.service.ServerService;
import com.example.reward_monitoring.statistics.saveMsn.detail.dto.SaveMsnDetailSearchDto;
import com.example.reward_monitoring.statistics.saveMsn.detail.entity.SaveMsnDetailsStat;
import com.example.reward_monitoring.statistics.saveMsn.detail.service.SaveMsnDetailService;
import com.example.reward_monitoring.statistics.searchMsn.detail.entity.SearchMsnDetailsStat;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/Statistics/statSightseeing")
public class SaveMsnDetailController {

    @Autowired
    SaveMsnDetailService saveMsnDetailService;

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    AdvertiserService advertiserService;
    @Autowired
    MediaCompanyService mediaCompanyService;
    @Autowired
    ServerService serverService;

    @Operation(summary = "정답미션 검색", description = "조건에 맞는 정답미션 디테일 통계을 검색합니다")
    @PostMapping({"/search","/search/","/search/{pageNumber}"})
    @ResponseBody
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
            @ApiResponse(responseCode = "500", description = "검색 중 예기치않은 오류발생")
    })
    public Map<String, Object> searchSearchMsn(@PathVariable(required = false,value = "pageNumber") Integer pageNumber, HttpSession session, @RequestBody SaveMsnDetailSearchDto dto){
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


        List<SaveMsnDetailsStat> result = saveMsnDetailService.searchSaveMsnDetail(dto);
        Collections.reverse(result);
        // 페이지 번호가 없으면 기본값 1 사용
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        int limit = 30;
        int startIndex = (pageNumber - 1) * limit;

        // 전체 리스트의 크기 체크
        List<SaveMsnDetailsStat> limitedresult;
        if (startIndex < result.size()) {
            int endIndex = Math.min(startIndex + limit, result.size());
            limitedresult = result.subList(startIndex, endIndex);
        } else {
            limitedresult = new ArrayList<>(); // 페이지 번호가 범위를 벗어난 경우 빈 리스트
        }

        int totalPages = (int) Math.ceil((double) result.size() / limit);
        int startPage = ((pageNumber - 1) / limit) * limit + 1; // 현재 페이지 그룹의 시작 페이지
        int endPage = Math.min(startPage + limit - 1, totalPages); // 현재 페이지 그룹의 끝 페이지

        response.put("saveMsnDailyStats", limitedresult);  // limitedMembers 리스트
        response.put("currentPage", pageNumber);  // 현재 페이지 번호
        response.put("totalPages", totalPages);    // 전체 페이지 수
        response.put("startPage",startPage);
        response.put("endPage",endPage);
        return response; // JSON 형태로 반환
    }

    @GetMapping({"/{pageNumber}","/",""})
    public String statSightseeing(@PathVariable(required = false,value = "pageNumber") Integer pageNumber, HttpSession session, Model model){
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

        List<SaveMsnDetailsStat> saveMsnDetailsStats = saveMsnDetailService.getSaveMsnsDetails();
        Collections.reverse(saveMsnDetailsStats);
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        // 한 페이지당 최대 10개 데이터
        int limit = 10;
        int startIndex = (pageNumber - 1) * limit;

        List<SaveMsnDetailsStat> limitedSaveMsnDetailsStats;
        if (startIndex < saveMsnDetailsStats.size()) {
            int endIndex = Math.min(startIndex + limit, saveMsnDetailsStats.size());
            limitedSaveMsnDetailsStats = saveMsnDetailsStats.subList(startIndex, endIndex);
        } else {
            limitedSaveMsnDetailsStats = new ArrayList<>(); // 페이지 번호가 범위를 벗어난 경우 빈 리스트
        }
        // 전체 페이지 수 계산
        int totalPages = (int) Math.ceil((double) saveMsnDetailsStats.size() / limit);
        int startPage = ((pageNumber - 1) / limit) * limit + 1; // 현재 페이지 그룹의 시작 페이지
        int endPage = Math.min(startPage + limit - 1, totalPages); // 현재 페이지 그룹의 끝 페이지

        model.addAttribute("saveMsnDetailsStats", limitedSaveMsnDetailsStats);
        model.addAttribute("servers", servers);
        model.addAttribute("advertisers", advertisers);
        model.addAttribute("mediaCompanys", mediaCompanys);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "statSightseeing";

    }
}
