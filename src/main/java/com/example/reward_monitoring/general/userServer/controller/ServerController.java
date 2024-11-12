package com.example.reward_monitoring.general.userServer.controller;


import com.example.reward_monitoring.general.advertiser.entity.Advertiser;
import com.example.reward_monitoring.general.member.entity.Member;
import com.example.reward_monitoring.general.member.model.Auth;
import com.example.reward_monitoring.general.member.repository.MemberRepository;
import com.example.reward_monitoring.general.userServer.dto.ServerEditDto;
import com.example.reward_monitoring.general.userServer.dto.ServerReadDto;
import com.example.reward_monitoring.general.userServer.dto.ServerSearchDto;
import com.example.reward_monitoring.general.userServer.entity.Server;
import com.example.reward_monitoring.general.userServer.repository.ServerRepository;
import com.example.reward_monitoring.general.userServer.service.ServerService;
import com.example.reward_monitoring.mission.answerMsn.entity.AnswerMsn;
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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.*;

@Controller
@Tag(name = "userServer", description = "사용자 서버 API")
@RequestMapping("/Site")
public class ServerController {

    @Autowired
    private ServerRepository serverRepository;
    @Autowired
    private ServerService serverService;
    @Autowired
    private MemberRepository memberRepository;



    @Operation(summary = "사용자 서버 정보 수정", description = "사용자 서버 정보를 수정합니다")
    @Parameter(name = "idx", description = "수정할 사용자 서버 정보의 IDX")
    @PostMapping("/edit/{idx}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 수정됨"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "404", description = "일치하는 서버정보 찾을 수 없음")
    })
    public ResponseEntity<Server> edit(HttpSession session,@PathVariable("idx") int idx, @RequestBody ServerEditDto dto, HttpServletResponse response){

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
    @PostMapping("/add")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 수정됨"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "백앤드 오류로 서버생성이 되지않음")
    })
    public ResponseEntity<Void> add(HttpSession session,@RequestBody ServerReadDto dto){

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
    @GetMapping("requestServer/{idx}")  //서버검색(ID)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "서버 정보 검색 완료 "),
            @ApiResponse(responseCode = "204", description = "일치하는 서버 정보를 찾을 수 없음"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
    })
    public ResponseEntity<Server> getServer(HttpSession session,@PathVariable("idx") int idx){
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
    @GetMapping("/servers")  //멤버 리스트 반환
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨")
    })
    public ResponseEntity<List<Server>> getServers(HttpSession session){

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
    @DeleteMapping("/delete/{idx}")  // 회원탈퇴
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 완료 "),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "404", description = "일치하는 정보가 없음"),
    })
    public ResponseEntity<Void> delete(HttpSession session,@PathVariable("idx") int idx)throws IOException {

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
    @PostMapping({"/search","/search/{pageNumber}"})
    @ResponseBody
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "검색 중 예기치않은 오류발생")
    })
    public Map<String, Object> searchServer(@PathVariable(required = false,value = "pageNumber") Integer pageNumber,HttpSession session, @RequestBody ServerSearchDto dto){
        Member sessionMember= (Member) session.getAttribute("member");
        Map<String, Object> response = new HashMap<>();
        if(sessionMember == null) {
            response.put("error", "404"); // 멤버가 없는 경우
            return response; // JSON 형태로 반환
        }
        Member member =memberRepository.findById( sessionMember.getId());
        if (member == null) {
            response.put("error", "404"); // 비권한 사용자인 경우
            return response; // JSON 형태로 반환
        }
        if(member.isNauthMember()){
            response.put("error", "403");
            return response;
        }

        List<Server> result = serverService.searchServer(dto);
        // 페이지 번호가 없으면 기본값 1 사용
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }

        // 한 페이지당 최대 15개 데이터
        int limit = 15;
        int startIndex = (pageNumber - 1) * limit;

        // 전체 리스트의 크기 체크
        List<Server> limitedServers;
        if (startIndex < result.size()) {
            int endIndex = Math.min(startIndex + limit, result.size());
            limitedServers = result.subList(startIndex, endIndex);
        } else {
            limitedServers = new ArrayList<>(); // 페이지 번호가 범위를 벗어난 경우 빈 리스트
        }


        int totalPages = (int) Math.ceil((double) result.size() / limit);
        int startPage = ((pageNumber - 1) / limit) * limit + 1; // 현재 페이지 그룹의 시작 페이지
        int endPage = Math.min(startPage + limit - 1, totalPages); // 현재 페이지 그룹의 끝 페이지

        response.put("servers", limitedServers);  // limitedMembers 리스트
        response.put("currentPage", pageNumber);  // 현재 페이지 번호
        response.put("totalPages", totalPages);    // 전체 페이지 수
        response.put("startPage",startPage);
        response.put("endPage",endPage);
        return response; // JSON 형태로 반환
    }

    @Operation(summary = "잘못된 URL 캐치 ", description = "검색중 재진입시 ")
    @GetMapping("/search/{pageNumber}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
    })
    public String  searchServer_return(@PathVariable(required = false,value = "pageNumber") Integer pageNumber,HttpSession session,Model model){
        Member sessionMember = (Member) session.getAttribute("member");
        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료
        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }

        List<Server> servers = serverService.getServers();

        // 페이지 번호가 없으면 기본값 1 사용
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }

        // 한 페이지당 최대 15개 데이터
        int limit = 15;
        int startIndex = (pageNumber - 1) * limit;

        // 전체 리스트의 크기 체크
        List<Server> limitedServers;
        if (startIndex < servers.size()) {
            int endIndex = Math.min(startIndex + limit, servers.size());
            limitedServers = servers.subList(startIndex, endIndex);
        } else {
            limitedServers = new ArrayList<>(); // 페이지 번호가 범위를 벗어난 경우 빈 리스트
        }

        int totalPages = (int) Math.ceil((double) servers.size() / limit);
        int startPage = ((pageNumber - 1) / limit) * limit + 1; // 현재 페이지 그룹의 시작 페이지
        int endPage = Math.min(startPage + limit - 1, totalPages); // 현재 페이지 그룹의 끝 페이지
        model.addAttribute("servers", limitedServers);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", (int) Math.ceil((double) servers.size() / limit));
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "userServerList";
    }


    @GetMapping({"/userServerList/{pageNumber}","/userServerList","/",""})
    public String userServerList(@PathVariable(required = false,value = "pageNumber") Integer pageNumber,HttpSession session, Model model) {
        Member sessionMember = (Member) session.getAttribute("member");
        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료
        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }

        List<Server> servers = serverService.getServers();

        // 페이지 번호가 없으면 기본값 1 사용
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }

        // 한 페이지당 최대 15개 데이터
        int limit = 15;
        int startIndex = (pageNumber - 1) * limit;

        // 전체 리스트의 크기 체크
        List<Server> limitedServers;
        if (startIndex < servers.size()) {
            int endIndex = Math.min(startIndex + limit, servers.size());
            limitedServers = servers.subList(startIndex, endIndex);
        } else {
            limitedServers = new ArrayList<>(); // 페이지 번호가 범위를 벗어난 경우 빈 리스트
        }




        int totalPages = (int) Math.ceil((double) servers.size() / limit);
        int startPage = ((pageNumber - 1) / limit) * limit + 1; // 현재 페이지 그룹의 시작 페이지
        int endPage = Math.min(startPage + limit - 1, totalPages); // 현재 페이지 그룹의 끝 페이지

        model.addAttribute("servers", limitedServers);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", (int) Math.ceil((double) servers.size() / limit));
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "userServerList";
    }

    @GetMapping("/userServerWrite")
    public String userServerWrite(HttpSession session){
        Member sessionMember = (Member) session.getAttribute("member");
        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료
        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }


        return "userServerWrite";
    }

    @GetMapping("/userServerWrite/{idx}")
    public String userServerEdit(HttpSession session, Model model,@PathVariable(required = false,value = "idx")int idx){
        Member sessionMember = (Member) session.getAttribute("member");
        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료
        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }
        Server server = serverService.getServer(idx);
        if(server==null)
            return "error/404";
        model.addAttribute("server", server);

        return "userServerWrite";
    }
}
