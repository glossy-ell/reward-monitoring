package com.example.reward_monitoring.statistics.searchMsn.daily.controller;



import com.example.reward_monitoring.general.advertiser.entity.Advertiser;
import com.example.reward_monitoring.general.advertiser.service.AdvertiserService;
import com.example.reward_monitoring.general.mediaCompany.entity.MediaCompany;
import com.example.reward_monitoring.general.mediaCompany.service.MediaCompanyService;
import com.example.reward_monitoring.general.member.entity.Member;
import com.example.reward_monitoring.general.member.repository.MemberRepository;
import com.example.reward_monitoring.general.userServer.entity.Server;
import com.example.reward_monitoring.general.userServer.service.ServerService;
import com.example.reward_monitoring.statistics.searchMsn.daily.dto.SearchMsnDailyStatSearchDto;
import com.example.reward_monitoring.statistics.searchMsn.daily.entity.SearchMsnDailyStat;
import com.example.reward_monitoring.statistics.searchMsn.daily.service.SearchMsnDailyService;
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
@RequestMapping("/Statistics/statDailySearch")
public class SearchMsnDailyStatController {

    @Autowired
    SearchMsnDailyService searchMsnDailyService;

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    AdvertiserService advertiserService;
    @Autowired
    MediaCompanyService mediaCompanyService;
    @Autowired
    ServerService serverService;

    @Operation(summary = "정답미션데일리 통계 검색", description = "조건에 맞는 정답미션 데일리 통계를 검색합니다")
    @PostMapping({"/search","/search/{pageNumber}"})
    @ResponseBody
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
            @ApiResponse(responseCode = "500", description = "검색 중 예기치않은 오류발생")
    })
    public Map<String, Object> searchSearchMsn(@PathVariable(required = false,value = "pageNumber") Integer pageNumber, HttpSession session, @RequestBody SearchMsnDailyStatSearchDto dto){
        Member sessionMember= (Member) session.getAttribute("member");

        Map<String, Object> response = new HashMap<>();
        if(sessionMember == null){
            response.put("error", "404"); // 멤버가 없는 경우
            return response;
        }

        Member member =memberRepository.findById( sessionMember.getId());
        if(member.isNauthSearchDaily()){
            response.put("error", "403");
            return response;
        }
        // 페이지 번호가 없으면 기본값 1 사용
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        List<SearchMsnDailyStat> result = searchMsnDailyService.searchSearchMsnDaily(dto);
        // 한 페이지당 최대 15개 데이터
        int limit = 15;
        int startIndex = (pageNumber - 1) * limit;

        // 전체 리스트의 크기 체크
        List<SearchMsnDailyStat> limitedSearchMsnDailyStats;

        if (startIndex < result.size()) {
            int endIndex = Math.min(startIndex + limit, result.size());
            limitedSearchMsnDailyStats = result.subList(startIndex, endIndex);
        } else {
            limitedSearchMsnDailyStats = new ArrayList<>(); // 페이지 번호가 범위를 벗어난 경우 빈 리스트
        }

        int totalPages = (int) Math.ceil((double) result.size() / limit);
        response.put("searchMsnDailyStats", limitedSearchMsnDailyStats);  // limitedMembers 리스트
        response.put("currentPage", pageNumber);  // 현재 페이지 번호
        response.put("totalPages", totalPages);    // 전체 페이지 수
        return response; // JSON 형태로 반환
    }

    @Operation(summary = "에러 방지", description = "검색 재진입시 원래 페이지로 리턴 ")
    @GetMapping("/search/{pageNumber}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
    })
    public String  searchSearchMsn_return(@PathVariable(required = false,value = "pageNumber") Integer pageNumber,HttpSession session ,Model model){
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

        LocalDate currentDate = LocalDate.now();
        LocalDate past = currentDate.minusMonths(1);
        List<SearchMsnDailyStat> searchMsnDailyStats = searchMsnDailyService.getSearchMsnsDailysMonth(currentDate,past);
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        // 한 페이지당 최대 10개 데이터
        int limit = 10;
        int startIndex = (pageNumber - 1) * limit;

        List<SearchMsnDailyStat> limitedSearchMsnDailyStats;
        if (startIndex <  searchMsnDailyStats.size()) {
            int endIndex = Math.min(startIndex + limit,  searchMsnDailyStats.size());
            limitedSearchMsnDailyStats = searchMsnDailyStats.subList(startIndex, endIndex);
        } else {
            limitedSearchMsnDailyStats = new ArrayList<>(); // 페이지 번호가 범위를 벗어난 경우 빈 리스트
        }
        // 전체 페이지 수 계산
        int totalPages = (int) Math.ceil((double) searchMsnDailyStats.size() / limit);
        int startPage = ((pageNumber - 1) / limit) * limit + 1; // 현재 페이지 그룹의 시작 페이지
        int endPage = Math.min(startPage + limit - 1, totalPages); // 현재 페이지 그룹의 끝 페이지


        model.addAttribute("searchMsnDailyStats", limitedSearchMsnDailyStats);
        model.addAttribute("servers", servers);
        model.addAttribute("advertisers", advertisers);
        model.addAttribute("mediaCompanys", mediaCompanys);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "statDailySearch";
    }


    @GetMapping("/SaveMsnDailyStats")  //전체 광고주 리스트 반환
    public ResponseEntity<List<SearchMsnDailyStat>> getSearchMsnDailyStats(){
        return ResponseEntity.status(HttpStatus.OK).body(searchMsnDailyService.getSearchMsnsDailys());
    }


    @Operation(summary = "엑셀 다운로드", description = "검색미션 데일리 통계 엑셀파일을 다운로드합니다")
    @GetMapping("/excel/download")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "500", description = "예기치않은 오류발생")
    })
    public ResponseEntity<Void> excelDownload(HttpServletResponse response)throws IOException {
        try (Workbook wb = new XSSFWorkbook()) {
            List<SearchMsnDailyStat> list = searchMsnDailyService.getSearchMsnsDailys();
            Sheet sheet = searchMsnDailyService.excelDownload(list,wb);

            if(sheet !=null) {
                String fileName = URLEncoder.encode("검색미션 데일리 리포트.xlsx", StandardCharsets.UTF_8);
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
    @GetMapping({"/{pageNumber}","/",""})

    public String statDailySearch(@PathVariable(required = false,value = "pageNumber") Integer pageNumber, HttpSession session, Model model){
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

        LocalDate currentDate = LocalDate.now();
        LocalDate past = currentDate.minusMonths(1);
        List<SearchMsnDailyStat> searchMsnDailyStats = searchMsnDailyService.getSearchMsnsDailysMonth(currentDate,past);
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        // 한 페이지당 최대 10개 데이터
        int limit = 10;
        int startIndex = (pageNumber - 1) * limit;

        List<SearchMsnDailyStat> limitedSearchMsnDailyStats;
        if (startIndex <  searchMsnDailyStats.size()) {
            int endIndex = Math.min(startIndex + limit,  searchMsnDailyStats.size());
            limitedSearchMsnDailyStats = searchMsnDailyStats.subList(startIndex, endIndex);
        } else {
            limitedSearchMsnDailyStats = new ArrayList<>(); // 페이지 번호가 범위를 벗어난 경우 빈 리스트
        }
        // 전체 페이지 수 계산
        int totalPages = (int) Math.ceil((double) searchMsnDailyStats.size() / limit);
        int startPage = ((pageNumber - 1) / limit) * limit + 1; // 현재 페이지 그룹의 시작 페이지
        int endPage = Math.min(startPage + limit - 1, totalPages); // 현재 페이지 그룹의 끝 페이지


        model.addAttribute("searchMsnDailyStats", limitedSearchMsnDailyStats);
        model.addAttribute("servers", servers);
        model.addAttribute("advertisers", advertisers);
        model.addAttribute("mediaCompanys", mediaCompanys);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "statDailySearch";
    }
}
