package com.example.reward_monitoring.statistics.saveMsn.sum.controller;


import com.example.reward_monitoring.statistics.saveMsn.sum.dto.SaveMsnSumStatSearchDto;
import com.example.reward_monitoring.statistics.saveMsn.sum.entity.SaveMsnSumStat;
import com.example.reward_monitoring.statistics.saveMsn.sum.service.SaveMsnSumStatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RequestMapping("/Statistics/statSumSightseeing")
public class SaveMsnSumStatController {

    @Autowired
    private SaveMsnSumStatService saveMsnSumStatService;

    @GetMapping("/SaveMsnSumStats")  //전체 광고주 리스트 반환
    public ResponseEntity<List<SaveMsnSumStat>> getSaveMsnSumStats(){
        return ResponseEntity.status(HttpStatus.OK).body(saveMsnSumStatService.getSaveMsnSumStats());
    }

    @Operation(summary = "정답미션 검색", description = "조건에 맞는 정답미션을 검색합니다")
    @PostMapping("/search")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
            @ApiResponse(responseCode = "500", description = "검색 중 예기치않은 오류발생")
    })
    public ResponseEntity<List<SaveMsnSumStat>> searchSaveMsnSum(@RequestBody SaveMsnSumStatSearchDto dto){
        List<SaveMsnSumStat> result = saveMsnSumStatService.searchSaveMsnSum(dto);
        return (result != null) ?
                ResponseEntity.status(HttpStatus.OK).body(result): // 일치하는 결과가 없을경우 빈 리스트 반환
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
