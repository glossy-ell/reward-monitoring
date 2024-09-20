package com.example.reward_monitoring.statistics.searchMsn.sum.controller;


import com.example.reward_monitoring.general.member.entity.Member;
import com.example.reward_monitoring.general.member.repository.MemberRepository;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/Statistics/statSumSearch")
public class SearchMsnSumStatController {

    @Autowired
    private SearchMsnSumStatService searchMsnSumStatService;

    @Autowired
    private MemberRepository memberRepository;

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
    public String statSumSearch(HttpSession session){
        Member sessionMember = (Member) session.getAttribute("member");
        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료
        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }
        return "statSumSearch";
    }
}
