package com.example.reward_monitoring.statistics.searchMsn.detail.controller;




import com.example.reward_monitoring.statistics.searchMsn.detail.dto.SearchMsnDetailSearchDto;
import com.example.reward_monitoring.statistics.searchMsn.detail.entity.SearchMsnDetailsStat;
import com.example.reward_monitoring.statistics.searchMsn.detail.service.SearchMsnDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/Statistics/statSearch")
public class SearchMsnDetailController {

    @Autowired
    SearchMsnDetailService searchMsnDetailService;

    @Operation(summary = "검색미션 검색", description = "조건에 맞는 검색미션 디테일 통계을 검색합니다")
    @PostMapping("/search")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
            @ApiResponse(responseCode = "500", description = "검색 중 예기치않은 오류발생")
    })
    public ResponseEntity<List<SearchMsnDetailsStat>> searchSearchMsn(HttpSession session, @RequestBody SearchMsnDetailSearchDto dto){
        List<SearchMsnDetailsStat> result = searchMsnDetailService.searchSearchMsnDetail(dto);
        return (result != null) ?
                ResponseEntity.status(HttpStatus.OK).body(result): // 일치하는 결과가 없을경우 빈 리스트 반환
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }


}
