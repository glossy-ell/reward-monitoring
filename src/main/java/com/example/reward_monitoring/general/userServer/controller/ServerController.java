package com.example.reward_monitoring.general.userServer.controller;


import com.example.reward_monitoring.general.member.entity.Member;
import com.example.reward_monitoring.general.member.model.Auth;
import com.example.reward_monitoring.general.member.repository.MemberRepository;
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
import jakarta.servlet.http.HttpSession;
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
    @Autowired
    private MemberRepository memberRepository;



    @Operation(summary = "사용자 서버 정보 수정", description = "사용자 서버 정보를 수정합니다")
    @Parameter(name = "idx", description = "수정할 사용자 서버 정보의 IDX")
    @PostMapping("server/edit/{idx}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 수정됨"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "404", description = "일치하는 서버정보 찾을 수 없음")
    })
    public ResponseEntity<Server> edit(HttpSession session,@PathVariable int idx, @RequestBody ServerEditDto dto, HttpServletResponse response){

        Member sessionMember= (Member) session.getAttribute("member");
        if(sessionMember == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } // 세션만료
        Member member =memberRepository.findById( sessionMember.getId());
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }//데이터 없음
        if(member.getAuthServer()== Auth.READ) // 읽기 권한만 존재할경우
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();



        Server server = serverService.edit(idx,dto);
        if(server == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(server);
    }

    @Operation(summary = "서버 정보 생성", description = "서버 정보를 생성합니다")
    @PostMapping("server/add")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 수정됨"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "백앤드 오류로 서버생성이 되지않음")
    })
    public ResponseEntity<Void> join(HttpSession session,@RequestBody ServerReadDto dto){

        Member sessionMember= (Member) session.getAttribute("member");
        if(sessionMember == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } // 세션만료
        Member member =memberRepository.findById( sessionMember.getId());
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }//데이터 없음
        if(member.getAuthServer()== Auth.READ) // 읽기 권한만 존재할경우
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        Server created = serverService.add(dto);
        serverRepository.save(created);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "서버 정보 요청", description = "IDX와 일치하는 단일 사용자 서버 정보를 반환합니다")
    @Parameter(name = "idx", description = "관리자 IDX")
    @GetMapping("server/{idx}")  //서버검색(ID)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "서버 정보 검색 완료 "),
            @ApiResponse(responseCode = "204", description = "일치하는 서버 정보를 찾을 수 없음"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
    })
    public ResponseEntity<Server> getServer(HttpSession session,@PathVariable int idx){
        Member sessionMember= (Member) session.getAttribute("member");
        if(sessionMember == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } // 세션만료
        Member member =memberRepository.findById( sessionMember.getId());
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }//데이터 없음

        Server target = serverService.getServer(idx);
        return (target != null) ?
                ResponseEntity.status(HttpStatus.OK).body(target):
                ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @Operation(summary = "전체 사용자 서버 정보 요청", description = "전체 관리자 정보를 반환합니다")
    @GetMapping("server/servers")  //멤버 리스트 반환
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨")
    })
    public ResponseEntity<List<Server>> getMembers(HttpSession session){

        Member sessionMember= (Member) session.getAttribute("member");
        if(sessionMember == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } // 세션만료
        Member member =memberRepository.findById( sessionMember.getId());
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }//데이터 없음

        return ResponseEntity.status(HttpStatus.OK).body(serverService.getServers());
    }




    @Operation(summary = "사용자 서버 삭제", description = "IDX와 일치하는 단일 사자정보를 삭제합니다")
    @Parameter(name = "idx", description = "관리자정보 IDX")
    @DeleteMapping("server/delete/{idx}")  // 회원탈퇴
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 완료 "),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "404", description = "일치하는 정보가 없음"),
    })
    public ResponseEntity<Void> delete(HttpSession session,@PathVariable int idx)throws IOException {

        Member sessionMember= (Member) session.getAttribute("member");
        if(sessionMember == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } // 세션만료
        Member member =memberRepository.findById( sessionMember.getId());
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }//데이터 없음
        if(member.getAuthServer()== Auth.READ) // 읽기 권한만 존재할경우
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        Server deleted  = serverService.delete(idx);
        return (deleted != null) ?
                ResponseEntity.status(HttpStatus.NO_CONTENT).build():
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

    }
    @Operation(summary = "사용자 서버 검색", description = "조건에 맞는 사용자 서버 목록을 검색합니다")
    @PostMapping("/server/search")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "500", description = "검색 중 예기치않은 오류발생")
    })
    public ResponseEntity<List<Server>> searchServer(HttpSession session,@RequestBody ServerSearchDto dto){
        Member sessionMember= (Member) session.getAttribute("member");
        if(sessionMember == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();//세션만료
        Member member =memberRepository.findById( sessionMember.getId());
        if (member == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); //데이터 없음

        List<Server> result = serverService.searchMember(dto);
        return (result != null) ?
                ResponseEntity.status(HttpStatus.OK).body(result):
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}
