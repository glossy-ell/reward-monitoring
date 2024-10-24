package com.example.reward_monitoring.statistics.searchMsn.sum.controller;


import com.example.reward_monitoring.general.advertiser.entity.Advertiser;
import com.example.reward_monitoring.general.advertiser.service.AdvertiserService;
import com.example.reward_monitoring.general.mediaCompany.entity.MediaCompany;
import com.example.reward_monitoring.general.mediaCompany.service.MediaCompanyService;
import com.example.reward_monitoring.general.member.entity.Member;
import com.example.reward_monitoring.general.member.repository.MemberRepository;
import com.example.reward_monitoring.general.userServer.entity.Server;
import com.example.reward_monitoring.general.userServer.service.ServerService;
import com.example.reward_monitoring.statistics.searchMsn.sum.dto.SearchMsnSumStatSearchDto;
import com.example.reward_monitoring.statistics.searchMsn.sum.entity.SearchMsnSumStat;
import com.example.reward_monitoring.statistics.searchMsn.sum.service.SearchMsnSumStatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

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
    @PostMapping({"/search/{pageNumber}","/search/","/search",})
    @ResponseBody
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
            @ApiResponse(responseCode = "500", description = "검색 중 예기치않은 오류발생")
    })
    public Map<String, Object> searchSearchMsn(@PathVariable(required = false,value = "pageNumber") Integer pageNumber, Model model, @RequestBody SearchMsnSumStatSearchDto dto, HttpSession session){
        Member sessionMember = (Member) session.getAttribute("member");
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

        if(member.isNauthSearchMsn()) { // 비권한 활성화시
            response.put("error", "403");
            return response;
        }

        List<SearchMsnSumStat> result = searchMsnSumStatService.searchSearchMsnSum(dto);
        Collections.reverse(result);
        // 페이지 번호가 없으면 기본값 1 사용
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }

        // 한 페이지당 최대 10개 데이터
        int limit = 10;
        int startIndex = (pageNumber - 1) * limit;

        // 전체 리스트의 크기 체크
        List<SearchMsnSumStat> limitedSearchMsns;
        if (startIndex < result.size()) {
            int endIndex = Math.min(startIndex + limit, result.size());
            limitedSearchMsns = result.subList(startIndex, endIndex);
        } else {
            limitedSearchMsns = new ArrayList<>(); // 페이지 번호가 범위를 벗어난 경우 빈 리스트
        }

        int totalPages = (int) Math.ceil((double) result.size() / limit);
        int startPage = ((pageNumber - 1) / limit) * limit + 1; // 현재 페이지 그룹의 시작 페이지
        int endPage = Math.min(startPage + limit - 1, totalPages); // 현재 페이지 그룹의 끝 페이지


        int totalLandingCount =  limitedSearchMsns.stream().mapToInt(SearchMsnSumStat::getLandingCnt).sum();  // 랜딩카운트 합
        int totalPartCount =   limitedSearchMsns.stream().mapToInt(SearchMsnSumStat::getPartCnt).sum();  // 참여카운트 합
        response.put("searchMsnSumStats", limitedSearchMsns);  // limitedMembers 리스트
        response.put("currentPage", pageNumber);  // 현재 페이지 번호
        response.put("totalPages", totalPages);    // 전체 페이지 수
        response.put("startPage",startPage);
        response.put("endPage",endPage);
        response.put("totalLandingCount",totalLandingCount);
        response.put("totalPartCount",totalPartCount);
        return response; // JSON 형태로 반환
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

        int totalLandingCount = searchMsnSumStats.stream().mapToInt(SearchMsnSumStat::getLandingCnt).sum();  // 랜딩카운트 합
        int totalPartCount =  searchMsnSumStats.stream().mapToInt(SearchMsnSumStat::getPartCnt).sum();  // 참여카운트 합
        model.addAttribute("searchMsnSumStats", searchMsnSumStats);
        model.addAttribute("servers", servers);
        model.addAttribute("advertisers", advertisers);
        model.addAttribute("mediaCompanys", mediaCompanys);
        model.addAttribute("totalLandingCount",totalLandingCount);
        model.addAttribute("totalPartCount",totalPartCount);
        return "statSumSearch";
    }

    @Operation(summary = "저장 엑셀 다운로드", description = "저장 엑셀파일을 다운로드합니다")
    @GetMapping("/excel/download")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "예기치않은 오류발생")
    })
    public ResponseEntity<Void> excelDownload(HttpServletResponse response)throws IOException {
        try (Workbook wb = new XSSFWorkbook()) {
            LocalDate currentDate = LocalDate.now();
            LocalDate past = currentDate.minusMonths(1);
            List<SearchMsnSumStat> list = searchMsnSumStatService. getSearchMsnSumStatsMonth(currentDate,past);
            int totalLandingCount = list.stream().mapToInt(SearchMsnSumStat::getLandingCnt).sum();  // 랜딩카운트 합
            int totalPartCount =  list.stream().mapToInt(SearchMsnSumStat::getPartCnt).sum();  // 참여카운트
            Sheet sheet = searchMsnSumStatService.excelDownloadCurrent(list,wb,totalLandingCount,totalPartCount);
            if(sheet !=null) {
                String fileName = URLEncoder.encode("현재 리스트 소진량(정답).xlsx", StandardCharsets.UTF_8);
                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
                wb.write(response.getOutputStream());
                response.flushBuffer();
                return ResponseEntity.status(HttpStatus.OK).build();
            }
            else
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }
}
