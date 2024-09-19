package com.example.reward_monitoring.statistics.searchMsn.daily.controller;



import com.example.reward_monitoring.statistics.searchMsn.daily.dto.SearchMsnDailyStatSearchDto;
import com.example.reward_monitoring.statistics.searchMsn.daily.entity.SearchMsnDailyStat;
import com.example.reward_monitoring.statistics.searchMsn.daily.service.SearchMsnDailyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@RequestMapping("/Statistics/statDailySearch")
public class SearchMsnDailyStatController {

    @Autowired
    SearchMsnDailyService searchMsnDailyService;

    @Operation(summary = "검색미션데일리 통계 검색", description = "조건에 맞는 검색미션 데일리 통계를 검색합니다")
    @PostMapping("/search")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
            @ApiResponse(responseCode = "500", description = "검색 중 예기치않은 오류발생")
    })
    public ResponseEntity<List<SearchMsnDailyStat>> searchAnswerMsn(@RequestBody SearchMsnDailyStatSearchDto dto){
        List<SearchMsnDailyStat> result = searchMsnDailyService.searchSearchMsnDaily(dto);
        return (result != null) ?
                ResponseEntity.status(HttpStatus.OK).body(result): // 일치하는 결과가 없을경우 빈 리스트 반환
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/SaveMsnDailyStats")  //전체 광고주 리스트 반환
    public ResponseEntity<List<SearchMsnDailyStat>> getSearchMsnDailyStats(){
        return ResponseEntity.status(HttpStatus.OK).body(searchMsnDailyService.getSearchMsnsDailys());
    }


    @Operation(summary = "엑셀 다운로드", description = "정답미션 데일리 통계 엑셀파일을 다운로드합니다")
    @GetMapping("/download")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "500", description = "예기치않은 오류발생")
    })
    public ResponseEntity<Void> excelDownload(HttpServletResponse response)throws IOException {
        try (Workbook wb = new XSSFWorkbook()) {
            List<SearchMsnDailyStat> list = searchMsnDailyService.getSearchMsnsDailys();
            Sheet sheet = searchMsnDailyService.excelDownload(list,wb);

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
}
