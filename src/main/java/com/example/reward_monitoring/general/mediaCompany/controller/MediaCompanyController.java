package com.example.reward_monitoring.general.mediaCompany.controller;

import com.example.reward_monitoring.general.mediaCompany.dto.MediaCompanyEditDto;
import com.example.reward_monitoring.general.mediaCompany.dto.MediaCompanyReadDto;
import com.example.reward_monitoring.general.mediaCompany.dto.MediaCompanySearchDto;
import com.example.reward_monitoring.general.mediaCompany.entity.MediaCompany;
import com.example.reward_monitoring.general.mediaCompany.repository.MediaCompanyRepository;
import com.example.reward_monitoring.general.mediaCompany.service.MediaCompanyService;

import com.example.reward_monitoring.general.member.dto.MemberSearchDto;
import com.example.reward_monitoring.general.member.entity.Member;
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
@Tag(name = "MediaCompany", description = "매체사 API")
public class MediaCompanyController {
    @Autowired
    private MediaCompanyRepository mediaCompanyRepository;

    @Autowired
    private MediaCompanyService mediaCompanyService;
    
    @Operation(summary = "매체사 정보 수정", description = "매체사 정보를 수정합니다")
    @Parameter(name = "idx", description = "수정할 매체사의 IDX")
    @PostMapping("/mediaCompany/edit/{idx}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 수정됨"),
            @ApiResponse(responseCode = "500", description = "일치하는 회원을 찾을 수 없음")
    })
    public ResponseEntity<MediaCompany> edit(@PathVariable int idx,@RequestBody MediaCompanyEditDto dto, HttpServletResponse response){

        MediaCompany mediaCompany = mediaCompanyService.edit(idx,dto);
        if(mediaCompany == null){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(mediaCompany);
    }
    @Operation(summary = "관리자 가입", description = "관리자 정보를 생성합니다")
    @PostMapping("/mediaCompany/add")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 수정됨"),
            @ApiResponse(responseCode = "500", description = "서버 오류로 계정생성이 되지않음")
    })
    public ResponseEntity<Void> add(@RequestBody MediaCompanyReadDto dto){
        MediaCompany created = mediaCompanyService.add(dto);
        if(created == null){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        mediaCompanyRepository.save(created);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @Operation(summary = "관리자 정보 요청", description = "IDX와 일치하는 단일 매체사정보를 반환합니다")
    @Parameter(name = "idx", description = "관리자 IDX")
    @GetMapping("mediaCompany/idx/{idx}")  //idx 검색
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "관리자 정보 검색 완료 "),
            @ApiResponse(responseCode = "204", description = "일치하는 회원을 찾을 수 없음")
    })
    public ResponseEntity<MediaCompany> getId(@PathVariable int idx){

        MediaCompany target = mediaCompanyService.getMediaCompany(idx);
        return (target != null) ?
                ResponseEntity.status(HttpStatus.OK).body(target):
                ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "전체 매체사 정보 요청", description = "전체 매체사 정보를 반환합니다")
    @GetMapping("mediaCompany/mediaCompanys")  //매체사 리스트 반환
    public ResponseEntity<List<MediaCompany>> getMediaCompanys(){
        return ResponseEntity.status(HttpStatus.OK).body(mediaCompanyService.getMediaCompanys());
    }
    @Operation(summary = "매체사 삭제", description = "IDX와 일치하는 단일 매체사정보를 삭제합니다")
    @Parameter(name = "idx", description = "매체사정보 IDX")
    @DeleteMapping("mediaCompany/delete/{idx}")  // 회원탈퇴
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 완료 "),
            @ApiResponse(responseCode = "400", description = "일치하는 정보가 없음")
    })
    public ResponseEntity<String> delete(@PathVariable int idx)throws IOException {
        MediaCompany deleted  = mediaCompanyService.delete(idx);
        return (deleted != null) ?
                ResponseEntity.status(HttpStatus.NO_CONTENT).build():
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @Operation(summary = "매체사 검색", description = "조건에 맞는 매체사를 검색합니다")
    @PostMapping("/meadiacompany/search")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
            @ApiResponse(responseCode = "500", description = "검색 중 예기치않은 오류발생")
    })
    public ResponseEntity<List<MediaCompany>> searchMediaCompany(@RequestBody MediaCompanySearchDto dto){
        List<MediaCompany> result = mediaCompanyService.searchMediaCompany(dto);
        return (result != null) ?
                ResponseEntity.status(HttpStatus.OK).body(result): // 일치하는 결과가 없을경우 빈 리스트 반환
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
