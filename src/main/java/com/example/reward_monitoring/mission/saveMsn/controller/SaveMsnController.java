package com.example.reward_monitoring.mission.saveMsn.controller;

import com.example.reward_monitoring.mission.saveMsn.dto.SaveMsnEditDto;
import com.example.reward_monitoring.mission.saveMsn.dto.SaveMsnReadDto;
import com.example.reward_monitoring.mission.saveMsn.entity.SaveMsn;
import com.example.reward_monitoring.mission.saveMsn.repository.SaveMsnRepository;
import com.example.reward_monitoring.mission.saveMsn.service.SaveMsnService;
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
@Tag(name = "saveMsn", description = "저장미션 API")
public class SaveMsnController {
    @Autowired
    private SaveMsnRepository saveMsnRepository;

    @Autowired
    private SaveMsnService saveMsnService;

    @Operation(summary = "저장미션 정보 수정", description = "저장미션 정보를 수정합니다")
    @Parameter(name = "idx", description = "수정할 저장미션의 IDX")
    @PostMapping("/saveMsn/edit/{idx}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 수정됨"),
            @ApiResponse(responseCode = "500", description = "일치하는 미션을 찾을 수 없음")
    })
    public ResponseEntity<SaveMsn> edit(@PathVariable int idx, @RequestBody SaveMsnEditDto dto, HttpServletResponse response){

        SaveMsn edited = saveMsnService.edit(idx,dto);

        if(edited == null){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        saveMsnRepository.save(edited);
        return ResponseEntity.status(HttpStatus.OK).body(edited);
    }
    
    @Operation(summary = "저장미션 생성", description = "저장미션 정보를 생성합니다")
    @PostMapping("/saveMsn/add")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 수정됨"),
            @ApiResponse(responseCode = "500", description = "서버 오류로 미션생성이 되지않음")
    })
    public ResponseEntity<Void> add(@RequestBody SaveMsnReadDto dto){
        SaveMsn created = saveMsnService.add(dto);
        if(created == null)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        saveMsnRepository.save(created);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @Operation(summary = "저장미션 정보 요청", description = "IDX와 일치하는 미션정보를 반환합니다")
    @Parameter(name = "idx", description = "관리자 IDX")
    @GetMapping("saveMsn/{idx}") 
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "미션 검색 완료 "),
            @ApiResponse(responseCode = "204", description = "일치하는 미션을 찾을 수 없음")
    })
    public ResponseEntity<SaveMsn> getSaveMsn(@PathVariable int idx){
        SaveMsn target = saveMsnService.getSaveMsn(idx);
        return (target != null) ?
                ResponseEntity.status(HttpStatus.OK).body(target):
                ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "전체 미션정보 요청", description = "전체 미션 정보를 반환합니다")
    @GetMapping("/saveMsn/saveMsns")  //전체 광고주 리스트 반환
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료"),
    })
    public ResponseEntity<List<SaveMsn>> getSaveMsns(){
        return ResponseEntity.status(HttpStatus.OK).body(saveMsnService.getSaveMsns());
    }

    @Operation(summary = "미션 삭제", description = "IDX와 일치하는 단일 미션정보를 삭제합니다")
    @Parameter(name = "idx", description = "미션 IDX")
    @DeleteMapping("saveMsn/delete/{idx}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 완료 "),
            @ApiResponse(responseCode = "400", description = "일치하는 미션이 없음")
    })
    public ResponseEntity<String> delete(@PathVariable int idx)throws IOException {

        SaveMsn deleted  = saveMsnService.delete(idx);
        return (deleted != null) ?
                ResponseEntity.status(HttpStatus.NO_CONTENT).build():
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

    }
}
