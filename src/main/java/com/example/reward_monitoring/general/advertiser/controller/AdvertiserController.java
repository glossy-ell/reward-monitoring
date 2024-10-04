package com.example.reward_monitoring.general.advertiser.controller;

import com.example.reward_monitoring.general.advertiser.dto.AdvertiserEditDto;
import com.example.reward_monitoring.general.advertiser.dto.AdvertiserReadDto;
import com.example.reward_monitoring.general.advertiser.dto.AdvertiserSearchDto;
import com.example.reward_monitoring.general.advertiser.entity.Advertiser;
import com.example.reward_monitoring.general.advertiser.repository.AdvertiserRepository;
import com.example.reward_monitoring.general.advertiser.service.AdvertiserService;
import com.example.reward_monitoring.general.member.entity.Member;
import com.example.reward_monitoring.general.member.model.Auth;
import com.example.reward_monitoring.general.member.repository.MemberRepository;
import com.example.reward_monitoring.general.userServer.entity.Server;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@Tag(name = "Advertiser", description = "광고주 API")
@RequestMapping("/Advertiser")
public class AdvertiserController {

    @Autowired
    private AdvertiserRepository advertiserRepository;
    @Autowired
    private AdvertiserService advertiserService;
    @Autowired
    private MemberRepository memberRepository;

    @Operation(summary = "광고주 정보 수정", description = "광고주 정보를 수정합니다")
    @Parameter(name = "idx", description = "수정할 광고주의 IDX")
    @PostMapping("/edit/{idx}") //UPDATED
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 수정됨"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "일치하는 회원을 찾을 수 없음")
    })
    public ResponseEntity<Advertiser> edit(HttpSession session,@PathVariable("idx") int idx, @RequestBody AdvertiserEditDto dto, HttpServletResponse response){
        Member sessionMember= (Member) session.getAttribute("member");
        if(sessionMember == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } // 세션만료
        Member member =memberRepository.findById( sessionMember.getId());
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }//데이터 없음
        if(member.getAuthAdvertiser()== Auth.READ) // 읽기 권한만 존재할경우
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();


        Advertiser edited = advertiserService.edit(idx,dto);

        if(edited == null){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        advertiserRepository.save(edited);
        return ResponseEntity.status(HttpStatus.OK).body(edited);
    }
    @Operation(summary = "광고주 정보 생성", description = "광고주 정보를 생성합니다")
    @PostMapping("/advertiser/add") // CREATED
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 수정됨"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "백앤드 오류로 서버생성이 되지않음")
    })
    public ResponseEntity<Void> add(HttpSession session, @RequestBody AdvertiserReadDto dto){

        Member sessionMember= (Member) session.getAttribute("member");
        if(sessionMember == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } // 세션만료
        Member member =memberRepository.findById( sessionMember.getId());
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }//데이터 없음
        if(member.getAuthAdvertiser()== Auth.READ) // 읽기 권한만 존재할경우
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        Advertiser created = advertiserService.add(dto);
        if(created == null)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        advertiserRepository.save(created);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @Operation(summary = "광고주 정보 요청", description = "IDX와 일치하는 단일 광고주정보를 반환합니다")
    @Parameter(name = "idx", description = "광고주 IDX")
    @GetMapping("advertiser/{idx}")  //광고주 검색 (idx)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "서버 정보 검색 완료 "),
            @ApiResponse(responseCode = "204", description = "일치하는 서버 정보를 찾을 수 없음"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
    })
    public ResponseEntity<Advertiser> getAdvertiser(HttpSession session, @PathVariable("idx") int idx){
        Member sessionMember= (Member) session.getAttribute("member");
        if(sessionMember == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } // 세션만료
        Member member =memberRepository.findById( sessionMember.getId());
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }//데이터 없음

        Advertiser target = advertiserService.getAdvertiser(idx);
        return (target != null) ?
                ResponseEntity.status(HttpStatus.OK).body(target):
                ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }
    
    
    @Operation(summary = "전체 광고주 정보 요청", description = "전체 광고주 정보를 반환합니다")
    @GetMapping("/advertiser/advertisers")  //전체 광고주 리스트 반환
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨")
    })
    public ResponseEntity<List<Advertiser>> getAdvertisers(HttpSession session){
        Member sessionMember= (Member) session.getAttribute("member");
        if(sessionMember == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } // 세션만료
        Member member =memberRepository.findById( sessionMember.getId());
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }//데이터 없음

        return ResponseEntity.status(HttpStatus.OK).body(advertiserService.getAdvertisers());
    }
    @Operation(summary = "광고주 삭제", description = "IDX와 일치하는 단일 광고주정보를 삭제합니다")
    @Parameter(name = "idx", description = "광고주 IDX")
    @DeleteMapping("advertiser/delete/{idx}")  // DELETE
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 완료 "),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "404", description = "일치하는 정보가 없음"),
    })
    public ResponseEntity<String> delete(HttpSession session,@PathVariable("idx") int idx)throws IOException{
        Member sessionMember= (Member) session.getAttribute("member");
        if(sessionMember == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } // 세션만료
        Member member =memberRepository.findById( sessionMember.getId());
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }//데이터 없음
        if(member.getAuthAdvertiser()== Auth.READ) // 읽기 권한만 존재할경우
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();


        Advertiser deleted  = advertiserService.delete(idx);
        return (deleted != null) ?
                ResponseEntity.status(HttpStatus.NO_CONTENT).build():
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

    }

    @Operation(summary = "광고주 검색", description = "조건에 맞는 광고주를 검색합니다")
    @PostMapping({"/advertiser/search","/advertiser/search/{pageNumber}"})
    @ResponseBody
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "검색 중 예기치않은 오류발생")
    })
    public Map<String, Object> searchAdvertiser(@PathVariable(required = false,value = "pageNumber") Integer pageNumber, HttpSession session, @RequestBody AdvertiserSearchDto dto){
        Member sessionMember= (Member) session.getAttribute("member");

        Map<String, Object> response = new HashMap<>();
        if(sessionMember == null){
            response.put("error", "404"); // 멤버가 없는 경우
            return response;
        }

        Member member =memberRepository.findById( sessionMember.getId());
        if (member == null) {
            response.put("error", "403"); // 비권한 사용자인 경우
            return response;
        }
        if(member.isNauthMember()){
            response.put("error", "403");
            return response;
        }


        List<Advertiser> result = advertiserService.searchAdvertiser(dto);
        // 페이지 번호가 없으면 기본값 1 사용
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        // 한 페이지당 최대 15개 데이터
        int limit = 15;
        int startIndex = (pageNumber - 1) * limit;

        // 전체 리스트의 크기 체크
        List<Advertiser> limitedAdvertisers;
        if (startIndex < result.size()) {
            int endIndex = Math.min(startIndex + limit, result.size());
            limitedAdvertisers = result.subList(startIndex, endIndex);
        } else {
            limitedAdvertisers = new ArrayList<>(); // 페이지 번호가 범위를 벗어난 경우 빈 리스트
        }


        int totalPages = (int) Math.ceil((double) result.size() / limit);
        response.put("advertisers", limitedAdvertisers);  // limitedMembers 리스트
        response.put("currentPage", pageNumber);  // 현재 페이지 번호
        response.put("totalPages", totalPages);    // 전체 페이지 수
        return response; // JSON 형태로 반환
    }


    @GetMapping({"/userServerList/{pageNumber}","/advertiserList","/",""})
    public String advertiserList(@PathVariable(required = false,value = "pageNumber") Integer pageNumber,HttpSession session, Model model){
        Member sessionMember = (Member) session.getAttribute("member");
        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료
        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }



        List<Advertiser> advertisers = advertiserService.getAdvertisers();

        // 페이지 번호가 없으면 기본값 1 사용
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }

        // 한 페이지당 최대 15개 데이터
        int limit = 15;
        int startIndex = (pageNumber - 1) * limit;

        // 전체 리스트의 크기 체크
        List<Advertiser> limitedAdvertisers;
        if (startIndex < advertisers.size()) {
            int endIndex = Math.min(startIndex + limit, advertisers.size());
            limitedAdvertisers = advertisers.subList(startIndex, endIndex);
        } else {
            limitedAdvertisers = new ArrayList<>(); // 페이지 번호가 범위를 벗어난 경우 빈 리스트
        }


        model.addAttribute("advertisers", advertisers);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", (int) Math.ceil((double) advertisers.size() / limit));
        return "/advertiserList";
    }




    @GetMapping("/advertiserWrite")
    public String userServerWrite(HttpSession session){
        Member sessionMember = (Member) session.getAttribute("member");
        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료
        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }

        return "advertiserWrite";
    }

    @GetMapping("/adminWrite/{idx}")
    public String adminEdit(HttpSession session,Model model,@PathVariable(required = false,value = "idx") int idx){

        Member sessionMember = (Member) session.getAttribute("member");
        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료
        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }

        Advertiser advertiser = advertiserService.getAdvertiser(idx);
        if(advertiser==null)
            return "error/404";

        model.addAttribute("advertiser", advertiser);
        return "adminWrite";
    }

}
