package com.example.reward_monitoring.general.userServer.controller;

import com.example.reward_monitoring.general.member.dto.MemberEditDto;
import com.example.reward_monitoring.general.member.dto.MemberReadDto;
import com.example.reward_monitoring.general.member.dto.MemberSearchDto;
import com.example.reward_monitoring.general.member.entity.Member;
import com.example.reward_monitoring.general.userServer.dto.ServerEditDto;
import com.example.reward_monitoring.general.userServer.dto.ServerReadDto;
import com.example.reward_monitoring.general.userServer.dto.ServerSearchDto;
import com.example.reward_monitoring.general.userServer.entity.Server;
import com.example.reward_monitoring.general.userServer.repository.ServerRepository;
import com.example.reward_monitoring.general.userServer.service.ServerService;
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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Controller
@Tag(name = "userServer", description = "사용자 서버 API")
public class ServerController {

    @Autowired
    private ServerRepository serverRepository;

    @Autowired
    private ServerService serverService;


    @Operation(summary = "사용자 서버 정보 수정", description = "사용자 서버 정보를 수정합니다")
    @Parameter(name = "idx", description = "수정할 사용자 서버 정보의 IDX")
    @PostMapping("server/edit/{idx}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 수정됨"),
            @ApiResponse(responseCode = "500", description = "일치하는 서버정보 찾을 수 없음")
    })
    public ResponseEntity<Server> edit(@PathVariable int idx, @RequestBody ServerEditDto dto, HttpServletResponse response){

        Server server = serverService.edit(idx,dto);
        if(server == null){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(server);
    }

    @Operation(summary = "서버 정보 생성", description = "서버 정보를 생성합니다")
    @PostMapping("server/add")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 수정됨"),
            @ApiResponse(responseCode = "500", description = "서버 오류로 계정생성이 되지않음")
    })
    public ResponseEntity<Void> join(@RequestBody ServerReadDto dto){
        Server created = serverService.add(dto);
        serverRepository.save(created);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "서버 정보 요청", description = "IDX와 일치하는 단일 사용자 서버 정보를 반환합니다")
    @Parameter(name = "idx", description = "관리자 IDX")
    @GetMapping("server/{idx}")  //서버검색(ID)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "서버 정보 검색 완료 "),
            @ApiResponse(responseCode = "204", description = "일치하는 서버 정보를 찾을 수 없음")
    })
    public ResponseEntity<Server> getServer(@PathVariable int idx){
        Server target = serverService.getServer(idx);
        return (target != null) ?
                ResponseEntity.status(HttpStatus.OK).body(target):
                ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @Operation(summary = "전체 사용자 서버 정보 요청", description = "전체 관리자 정보를 반환합니다")
    @GetMapping("server/servers")  //멤버 리스트 반환
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료"),
    })
    public ResponseEntity<List<Server>> getMembers(){
        return ResponseEntity.status(HttpStatus.OK).body(serverService.getServers());
    }
    @Operation(summary = "사용자 서버 삭제", description = "IDX와 일치하는 단일 사자정보를 삭제합니다")
    @Parameter(name = "idx", description = "관리자정보 IDX")
    @DeleteMapping("server/delete/{idx}")  // 회원탈퇴
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 완료 "),
            @ApiResponse(responseCode = "400", description = "일치하는 정보가 없음")
    })
    public ResponseEntity<Void> delete(@PathVariable int idx)throws IOException {

        Server deleted  = serverService.delete(idx);
        return (deleted != null) ?
                ResponseEntity.status(HttpStatus.NO_CONTENT).build():
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

    }
    @Operation(summary = "미구현", description = "해당 API는 아직 구현되지않음")
    @GetMapping("/server/search")
    public ResponseEntity<List<Server>> searchServer(@RequestBody ServerSearchDto dto){
        List<Server> result = serverService.searchMember(dto);
        return (result != null) ?
                ResponseEntity.status(HttpStatus.OK).body(result):
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

}
