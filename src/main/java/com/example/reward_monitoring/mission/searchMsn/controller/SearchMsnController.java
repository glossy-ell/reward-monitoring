package com.example.reward_monitoring.mission.searchMsn.controller;



import com.example.reward_monitoring.mission.saveMsn.dto.SaveMsnEditDto;
import com.example.reward_monitoring.mission.searchMsn.dto.SearchMsnEditDto;
import com.example.reward_monitoring.mission.searchMsn.dto.SearchMsnReadDto;
import com.example.reward_monitoring.mission.searchMsn.entity.SearchMsn;
import com.example.reward_monitoring.mission.searchMsn.repository.SearchMsnRepositroy;
import com.example.reward_monitoring.mission.searchMsn.service.SearchMsnService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@Tag(name = "SearchMsn", description = "검색미션 API")
public class SearchMsnController {
    @Autowired
    private SearchMsnRepositroy searchMsnRepositroy;

    @Autowired
    private SearchMsnService searchMsnService;

    @Operation(summary = "검색미션 정보 수정", description = "검색미션 정보를 수정합니다")
    @Parameter(name = "idx", description = "수정할 검색미션의 IDX")
    @PostMapping("/searchMsn/edit/{idx}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 수정됨"),
            @ApiResponse(responseCode = "500", description = "일치하는 미션을 찾을 수 없음")
    })
    public ResponseEntity<SearchMsn> edit(@PathVariable int idx, @RequestBody SearchMsnEditDto dto, HttpServletResponse response){

        SearchMsn edited = searchMsnService.edit(idx,dto);

        if(edited == null){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        searchMsnRepositroy.save(edited);
        return ResponseEntity.status(HttpStatus.OK).body(edited);
    }
    
    @Operation(summary = "검색미션 생성", description = "검색미션 정보를 생성합니다")
    @PostMapping("/searchMsn/add")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 수정됨"),
            @ApiResponse(responseCode = "500", description = "서버 오류로 미션생성이 되지않음")
    })
    public ResponseEntity<Void> add(@RequestBody SearchMsnReadDto dto){
        SearchMsn created = searchMsnService.add(dto);
        if(created == null)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        searchMsnRepositroy.save(created);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @Operation(summary = "검색미션 정보 요청", description = "IDX와 일치하는 미션정보를 반환합니다")
    @Parameter(name = "idx", description = "관리자 IDX")
    @GetMapping("searchMsn/{idx}")  //미션 검색 (idx)
    public ResponseEntity<SearchMsn> getSaveMsn(@PathVariable int idx){
        SearchMsn target = searchMsnService.getSearchMsn(idx);
        return (target != null) ?
                ResponseEntity.status(HttpStatus.OK).body(target):
                ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @Operation(summary = "전체 미션정보 요청", description = "전체 미션 정보를 반환합니다")
    @GetMapping("/searchMsn/searchMsns")  //전체 광고주 리스트 반환
    public ResponseEntity<List<SearchMsn>> getAnswerMsns(){
        return ResponseEntity.status(HttpStatus.OK).body(searchMsnService.getSearchMsns());
    }

    @Operation(summary = "미션 삭제", description = "IDX와 일치하는 단일 미션정보를 삭제합니다")
    @Parameter(name = "idx", description = "미션 IDX")
    @DeleteMapping("searchMsn/delete/{idx}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 완료 "),
            @ApiResponse(responseCode = "400", description = "일치하는 미션이 없음")
    })
    public ResponseEntity<String> delete(@PathVariable int idx)throws IOException {

        SearchMsn deleted  = searchMsnService.delete(idx);
        return (deleted != null) ?
                ResponseEntity.status(HttpStatus.NO_CONTENT).build():
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

    }
}
