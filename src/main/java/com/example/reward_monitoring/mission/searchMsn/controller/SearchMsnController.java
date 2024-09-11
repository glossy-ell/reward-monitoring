package com.example.reward_monitoring.mission.searchMsn.controller;



import com.example.reward_monitoring.mission.answerMsn.dto.AnswerMsnSearchDto;
import com.example.reward_monitoring.mission.answerMsn.entity.AnswerMsn;
import com.example.reward_monitoring.mission.searchMsn.dto.SearchMsnEditDto;
import com.example.reward_monitoring.mission.searchMsn.dto.SearchMsnReadDto;
import com.example.reward_monitoring.mission.searchMsn.dto.SearchMsnSearchDto;
import com.example.reward_monitoring.mission.searchMsn.entity.SearchMsn;
import com.example.reward_monitoring.mission.searchMsn.repository.SearchMsnRepository;

import com.example.reward_monitoring.mission.searchMsn.service.SearchMsnService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@Tag(name = "SearchMsn", description = "검색미션 API")
public class SearchMsnController {
    @Autowired
    private SearchMsnRepository searchMsnRepository;

    @Autowired
    private SearchMsnService searchMsnService;

    @Operation(summary = "검색미션 정보 수정", description = "검색미션 정보를 수정합니다")
    @Parameter(name = "idx", description = "수정할 검색미션의 IDX")
    @PostMapping("/searchMsn/edit/{idx}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 수정됨"),
            @ApiResponse(responseCode = "500", description = "일치하는 미션을 찾을 수 없음")
    })
    public ResponseEntity<SearchMsn> edit(@PathVariable int idx, @RequestBody SearchMsnEditDto dto, HttpServletResponse response) {

        SearchMsn edited = searchMsnService.edit(idx, dto);

        if (edited == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        searchMsnRepository.save(edited);
        return ResponseEntity.status(HttpStatus.OK).body(edited);
    }

    @Operation(summary = "검색미션 생성", description = "검색미션 정보를 생성합니다")
    @PostMapping("/searchMsn/add")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "성공적으로 생성됨"),
            @ApiResponse(responseCode = "500", description = "서버 오류로 미션생성이 되지않음")
    })
    public ResponseEntity<Void> add(@RequestBody SearchMsnReadDto dto) {
        SearchMsn created = searchMsnService.add(dto);
        if (created == null)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        searchMsnRepository.save(created);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "검색미션 정보 요청", description = "IDX와 일치하는 미션정보를 반환합니다")
    @Parameter(name = "idx", description = "관리자 IDX")
    @GetMapping("searchMsn/{idx}")  //미션 검색 (idx)
    public ResponseEntity<SearchMsn> getSaveMsn(@PathVariable int idx) {
        SearchMsn target = searchMsnService.getSearchMsn(idx);
        return (target != null) ?
                ResponseEntity.status(HttpStatus.OK).body(target) :
                ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "전체 미션정보 요청", description = "전체 미션 정보를 반환합니다")
    @GetMapping("/searchMsn/searchMsns")  //전체 광고주 리스트 반환
    public ResponseEntity<List<SearchMsn>> getAnswerMsns() {
        return ResponseEntity.status(HttpStatus.OK).body(searchMsnService.getSearchMsns());
    }

    @Operation(summary = "미션 삭제", description = "IDX와 일치하는 단일 미션정보를 삭제합니다")
    @Parameter(name = "idx", description = "미션 IDX")
    @DeleteMapping("searchMsn/delete/{idx}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 완료 "),
            @ApiResponse(responseCode = "400", description = "일치하는 미션이 없음")
    })
    public ResponseEntity<String> delete(@PathVariable int idx) throws IOException {

        SearchMsn deleted = searchMsnService.delete(idx);
        return (deleted != null) ?
                ResponseEntity.status(HttpStatus.NO_CONTENT).build() :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

    }

    @Operation(summary = "검색미션 검색", description = "조건에 맞는 검색미션을 검색합니다")
    @PostMapping("/searchMsn/search")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
            @ApiResponse(responseCode = "500", description = "검색 중 예기치않은 오류발생")
    })
    public ResponseEntity<List<SearchMsn>> searchAnswerMsn(@RequestBody SearchMsnSearchDto dto){
        List<SearchMsn> result = searchMsnService.searchSearchMsn(dto);
        return (result != null) ?
                ResponseEntity.status(HttpStatus.OK).body(result): // 일치하는 결과가 없을경우 빈 리스트 반환
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    
    @Operation(summary = "엑셀 다운로드", description = "검색미션 리스트 엑셀파일을 다운로드합니다")
    @GetMapping("/searchMsn/excel/download")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "500", description = "예기치않은 오류발생")
    })
    public ResponseEntity<Void> excelDownload(HttpServletResponse response) throws IOException {
        try (Workbook wb = new XSSFWorkbook()) {
            List<SearchMsn> list = searchMsnService.getSearchMsns();
            Sheet sheet = searchMsnService.excelDownload(list, wb);

            if (sheet != null) {
                String fileName = URLEncoder.encode("검색미션 리스트.xlsx", StandardCharsets.UTF_8);
                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
                wb.write(response.getOutputStream());
                response.flushBuffer();
                return ResponseEntity.status(HttpStatus.OK).build();
            } else
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }


    }

    @Operation(summary = "엑셀 업로드", description = "업로드한 엑셀파일을 DTO로 변환하여 DB에 추가합니다.")
    @PostMapping("/searchMsn/excel/upload")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "500", description = "엑셀파일의 문제로 인한 데이터 삽입 실패")
    })
    public ResponseEntity<Void> excelUpload(@RequestParam("file") MultipartFile file)throws IOException {
        boolean result = searchMsnService.readExcel(file);

        return (result) ?
                ResponseEntity.status(HttpStatus.OK).build():
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}
