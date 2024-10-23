package com.example.reward_monitoring.statistics.saveMsn.daily.controller;


import com.example.reward_monitoring.general.advertiser.entity.Advertiser;
import com.example.reward_monitoring.general.advertiser.service.AdvertiserService;
import com.example.reward_monitoring.general.mediaCompany.entity.MediaCompany;
import com.example.reward_monitoring.general.mediaCompany.service.MediaCompanyService;
import com.example.reward_monitoring.general.member.entity.Member;
import com.example.reward_monitoring.general.member.repository.MemberRepository;
import com.example.reward_monitoring.general.userServer.entity.Server;
import com.example.reward_monitoring.general.userServer.service.ServerService;
import com.example.reward_monitoring.statistics.answerMsnStat.detail.entity.AnswerMsnDetailsStat;
import com.example.reward_monitoring.statistics.saveMsn.daily.dto.SaveMsnDailyStatSearchDto;
import com.example.reward_monitoring.statistics.saveMsn.daily.entity.SaveMsnDailyStat;
import com.example.reward_monitoring.statistics.saveMsn.daily.service.SaveMsnDailyService;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/Statistics/statDailySightseeing")
public class SaveMsnDailyStatController {

    @Autowired
    SaveMsnDailyService saveMsnDailyService;

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    AdvertiserService advertiserService;
    @Autowired
    MediaCompanyService mediaCompanyService;
    @Autowired
    ServerService serverService;

    @Operation(summary = "검색미션데일리 통계 검색", description = "조건에 맞는 검색미션 데일리 통계를 검색합니다")
    @PostMapping("/search")
    @ResponseBody
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
            @ApiResponse(responseCode = "500", description = "검색 중 예기치않은 오류발생")
    })
    public ResponseEntity<List<SaveMsnDailyStat>> searchsaveMsn(@RequestBody SaveMsnDailyStatSearchDto dto){
        List<SaveMsnDailyStat> result = saveMsnDailyService.searchSaveMsnDaily(dto);
        return (result != null) ?
                ResponseEntity.status(HttpStatus.OK).body(result): // 일치하는 결과가 없을경우 빈 리스트 반환
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/SaveMsnDailyStats")  //전체 광고주 리스트 반환
    public ResponseEntity<List<SaveMsnDailyStat>> getSaveMsnDailyStats(){
        return ResponseEntity.status(HttpStatus.OK).body(saveMsnDailyService.getSaveMsnsDailys());
    }


    @Operation(summary = "엑셀 다운로드", description = "정답미션 데일리 통계 엑셀파일을 다운로드합니다")
    @GetMapping("/download")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "500", description = "예기치않은 오류발생")
    })
    public ResponseEntity<Void> excelDownload(HttpServletResponse response)throws IOException {
        try (Workbook wb = new XSSFWorkbook()) {
            List<SaveMsnDailyStat> list = saveMsnDailyService.getSaveMsnsDailys();
            Sheet sheet = saveMsnDailyService.excelDownload(list,wb);

            if(sheet !=null) {
                String fileName = URLEncoder.encode("정답미션 데일리 리포트.xlsx", StandardCharsets.UTF_8);
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
    public String statDailySightseeing(@PathVariable(required = false,value = "pageNumber") Integer pageNumber, HttpSession session, Model model){
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
        List<SaveMsnDailyStat> saveMsnDailyStats = saveMsnDailyService.getSaveMsnsDailys();
        Collections.reverse(saveMsnDailyStats);
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        // 한 페이지당 최대 10개 데이터
        int limit = 10;
        int startIndex = (pageNumber - 1) * limit;


        // 전체 리스트의 크기 체크
        List<SaveMsnDailyStat> limitedSaveMsnDailyStats;
        if (startIndex < saveMsnDailyStats.size()) {
            int endIndex = Math.min(startIndex + limit, saveMsnDailyStats.size());
            limitedSaveMsnDailyStats = saveMsnDailyStats.subList(startIndex, endIndex);
        } else {
            limitedSaveMsnDailyStats = new ArrayList<>(); // 페이지 번호가 범위를 벗어난 경우 빈 리스트
        }
        // 전체 페이지 수 계산
        int totalPages = (int) Math.ceil((double) saveMsnDailyStats.size() / limit);
        int startPage = ((pageNumber - 1) / limit) * limit + 1; // 현재 페이지 그룹의 시작 페이지
        int endPage = Math.min(startPage + limit - 1, totalPages); // 현재 페이지 그룹의 끝 페이지

        model.addAttribute("saveMsnDailyStats", limitedSaveMsnDailyStats);
        model.addAttribute("servers", servers);
        model.addAttribute("advertisers", advertisers);
        model.addAttribute("mediaCompanys", mediaCompanys);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "statDailySightseeing";
    }
}
