package com.example.reward_monitoring.general.mediaCompany.controller;

import com.example.reward_monitoring.general.advertiser.entity.Advertiser;
import com.example.reward_monitoring.general.mediaCompany.dto.*;
import com.example.reward_monitoring.general.mediaCompany.entity.MediaCompany;
import com.example.reward_monitoring.general.mediaCompany.repository.MediaCompanyRepository;
import com.example.reward_monitoring.general.mediaCompany.service.MediaCompanyService;



import com.example.reward_monitoring.general.member.entity.Member;
import com.example.reward_monitoring.general.member.model.Auth;
import com.example.reward_monitoring.general.member.repository.MemberRepository;
import com.example.reward_monitoring.general.userServer.entity.Server;
import com.example.reward_monitoring.general.userServer.service.ServerService;
import com.example.reward_monitoring.mission.answerMsn.entity.AnswerMsn;
import com.example.reward_monitoring.mission.answerMsn.service.AnswerMsnService;
import com.example.reward_monitoring.mission.saveMsn.entity.SaveMsn;
import com.example.reward_monitoring.mission.saveMsn.service.SaveMsnService;
import com.example.reward_monitoring.mission.searchMsn.entity.SearchMsn;
import com.example.reward_monitoring.mission.searchMsn.service.SearchMsnService;
import com.example.reward_monitoring.statistics.answerMsnStat.daily.entity.AnswerMsnDailyStat;
import com.example.reward_monitoring.statistics.answerMsnStat.daily.service.AnswerMsnDailyService;
import com.example.reward_monitoring.statistics.answerMsnStat.sum.Service.AnswerMsnSumStatService;
import com.example.reward_monitoring.statistics.answerMsnStat.sum.entity.AnswerMsnSumStat;
import com.example.reward_monitoring.statistics.saveMsn.daily.entity.SaveMsnDailyStat;
import com.example.reward_monitoring.statistics.saveMsn.daily.service.SaveMsnDailyService;
import com.example.reward_monitoring.statistics.saveMsn.sum.entity.SaveMsnSumStat;
import com.example.reward_monitoring.statistics.saveMsn.sum.service.SaveMsnSumStatService;
import com.example.reward_monitoring.statistics.searchMsn.daily.entity.SearchMsnDailyStat;
import com.example.reward_monitoring.statistics.searchMsn.daily.service.SearchMsnDailyService;
import com.example.reward_monitoring.statistics.searchMsn.sum.entity.SearchMsnSumStat;
import com.example.reward_monitoring.statistics.searchMsn.sum.service.SearchMsnSumStatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@Tag(name = "MediaCompany", description = "매체사 API")
@RequestMapping("/Affiliate")
@Slf4j
public class MediaCompanyController {
    @Autowired
    private MediaCompanyRepository mediaCompanyRepository;

    @Autowired
    private MediaCompanyService mediaCompanyService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ServerService serverService;

    @Autowired
    private AnswerMsnDailyService answerMsnDailyService;
    @Autowired
    private AnswerMsnSumStatService answerMsnSumStatService;
    @Autowired
    private AnswerMsnService answerMsnService;

    @Autowired
    private SaveMsnDailyService saveMsnDailyService;
    @Autowired
    private SaveMsnSumStatService saveMsnSumStatService;
    @Autowired
    private SaveMsnService saveMsnService;

    @Autowired
    private SearchMsnDailyService searchMsnDailyService;
    @Autowired
    private SearchMsnSumStatService searchMsnSumStatService;
    @Autowired
    private SearchMsnService searchMsnService;


    @Operation(summary = "매체사 정보 수정", description = "매체사 정보를 수정합니다")
    @Parameter(name = "idx", description = "수정할 매체사의 IDX")
    @PostMapping("/edit/{idx}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 수정됨"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "404", description = "일치하는 회원을 찾을 수 없음")
    })
    public ResponseEntity<MediaCompany> edit(HttpSession session, @PathVariable("idx") int idx, @RequestBody MediaCompanyEditDto dto, HttpServletResponse response){

        Member sessionMember= (Member) session.getAttribute("member");
        if(sessionMember == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } // 세션만료
        Member member =memberRepository.findById( sessionMember.getId());
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }//데이터 없음
        if(member.getAuthMediacompany()== Auth.READ) // 읽기 권한만 존재할경우
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();


        MediaCompany mediaCompany = mediaCompanyService.edit(idx,dto);
        if(mediaCompany == null){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(mediaCompany);

    }
    @Operation(summary = "관리자 가입", description = "관리자 정보를 생성합니다")
    @PostMapping("/add")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 수정됨"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류로 계정생성이 되지않음")
    })
    public ResponseEntity<Void> add(HttpSession session,@RequestBody MediaCompanyReadDto dto){

        Member sessionMember= (Member) session.getAttribute("member");
        if(sessionMember == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } // 세션만료
        Member member =memberRepository.findById( sessionMember.getId());
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }//데이터 없음
        if(member.getAuthMediacompany()== Auth.READ) // 읽기 권한만 존재할경우
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();


        MediaCompany created = mediaCompanyService.add(dto);
        if(created == null){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        mediaCompanyRepository.save(created);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @Operation(summary = "매체사 정보 요청", description = "IDX와 일치하는 단일 매체사정보를 반환합니다")
    @Parameter(name = "idx", description = "관리자 IDX")
    @GetMapping("/idx/{idx}")  //idx 검색
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "관리자 정보 검색 완료 "),
            @ApiResponse(responseCode = "204", description = "일치하는 회원을 찾을 수 없음"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨")
    })
    public ResponseEntity<MediaCompany> getId(HttpSession session,@PathVariable("idx") int idx){
        Member sessionMember= (Member)session.getAttribute("member");
        if(sessionMember == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } // 세션만료
        Member member =memberRepository.findById( sessionMember.getId());
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }//데이터 없음


        MediaCompany target = mediaCompanyService.getMediaCompany(idx);
        return (target != null) ?
                ResponseEntity.status(HttpStatus.OK).body(target):
                ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "전체 매체사 정보 요청", description = "전체 매체사 정보를 반환합니다")
    @GetMapping("/Affiliates")  //매체사 리스트 반환
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 완료 "),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "500", description = "검색중 오류 발생")
    })
    public ResponseEntity<List<MediaCompany>> getMediaCompanys(HttpSession session){

        Member sessionMember= (Member)session.getAttribute("member");
        if(sessionMember == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } // 세션만료
        Member member =memberRepository.findById( sessionMember.getId());
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }//데이터 없음

        return ResponseEntity.status(HttpStatus.OK).body(mediaCompanyService.getMediaCompanys());
    }
    @Operation(summary = "매체사 삭제", description = "IDX와 일치하는 단일 매체사정보를 삭제합니다")
    @Parameter(name = "idx", description = "매체사정보 IDX")
    @DeleteMapping("/delete/{idx}")  // 회원탈퇴
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 완료 "),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "삭제중 오류 발생")
    })
    public ResponseEntity<String> delete(HttpSession session, @PathVariable("idx") int idx)throws IOException {
        Member sessionMember= (Member) session.getAttribute("member");
        if(sessionMember == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } // 세션만료
        Member member =memberRepository.findById( sessionMember.getId());
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }//데이터 없음
        if(member.getAuthMediacompany()== Auth.READ) // 읽기 권한만 존재할경우
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();


        MediaCompany deleted  = mediaCompanyService.delete(idx);
        return (deleted != null) ?
                ResponseEntity.status(HttpStatus.NO_CONTENT).build():
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }


    @Operation(summary = "매체사 검색", description = "조건에 맞는 매체사를 검색합니다")
    @PostMapping({"/search","/search/{pageNumber}","/search/"})
    @ResponseBody
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "500", description = "검색 중 예기치않은 오류발생")
    })
    public Map<String, Object> searchMediaCompany(@PathVariable(required = false,value = "pageNumber") Integer pageNumber,HttpSession session, @RequestBody MediaCompanySearchDto dto){
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

        List<MediaCompany> result = mediaCompanyService.searchMediaCompany(dto);


        // 페이지 번호가 없으면 기본값 1 사용
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }

        // 한 페이지당 최대 15개 데이터
        int limit = 15;
        int startIndex = (pageNumber - 1) * limit;
        // 전체 리스트의 크기 체크
        List<MediaCompany> limitedMediaCompanys;
        if (startIndex < result.size()) {
            int endIndex = Math.min(startIndex + limit, result.size());
            limitedMediaCompanys = result.subList(startIndex, endIndex);
        } else {
            limitedMediaCompanys = new ArrayList<>(); // 페이지 번호가 범위를 벗어난 경우 빈 리스트
        }

        int totalPages = (int) Math.ceil((double) result.size() / limit);
        int startPage = ((pageNumber - 1) / limit) * limit + 1; // 현재 페이지 그룹의 시작 페이지
        int endPage = Math.min(startPage + limit - 1, totalPages); // 현재 페이지 그룹의 끝 페이지


        response.put("affiliates", limitedMediaCompanys);  // limitedMembers 리스트
        response.put("currentPage", pageNumber);  // 현재 페이지 번호
        response.put("totalPages", totalPages);    // 전체 페이지 수
        response.put("startPage",startPage);
        response.put("endPage",endPage);
        return response; // JSON 형태로 반환

    }

    @Operation(summary = "잘못된 URL 캐치 ", description = "검색중 재진입시 ")
    @GetMapping("/affiliateList/search/{pageNumber}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
    })
    public String  searchAffiliate_return(@PathVariable(required = false,value = "pageNumber") Integer pageNumber,HttpSession session,Model model){
        Member sessionMember = (Member) session.getAttribute("member");
        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료
        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }

        List<MediaCompany> mediaCompanyList = mediaCompanyService.getMediaCompanys();
        // 페이지 번호가 없으면 기본값 1 사용
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        // 한 페이지당 최대 15개 데이터
        int limit = 15;
        int startIndex = (pageNumber - 1) * limit;

        // 전체 리스트의 크기 체크
        List<MediaCompany> limitedMediaCompanys;
        if (startIndex < mediaCompanyList.size()) {
            int endIndex = Math.min(startIndex + limit, mediaCompanyList.size());
            limitedMediaCompanys =  mediaCompanyList.subList(startIndex, endIndex);
        } else {
            limitedMediaCompanys = new ArrayList<>(); // 페이지 번호가 범위를 벗어난 경우 빈 리스트
        }

        int totalPages = (int) Math.ceil((double) mediaCompanyList.size() / limit);
        int startPage = ((pageNumber - 1) / limit) * limit + 1; // 현재 페이지 그룹의 시작 페이지
        int endPage = Math.min(startPage + limit - 1, totalPages); // 현재 페이지 그룹의 끝 페이지
        model.addAttribute("mediaCompanyList", limitedMediaCompanys);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", (int) Math.ceil((double) mediaCompanyList.size() / limit));
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "affiliateList";
    }


    @GetMapping({"/affiliateList/{pageNumber}","/affiliateList","/",""})
    public String affiliateList(@PathVariable(required = false,value = "pageNumber") Integer pageNumber ,HttpSession session, Model model) {
        Member sessionMember = (Member) session.getAttribute("member");
        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료
        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }

        List<MediaCompany> mediaCompanyList = mediaCompanyService.getMediaCompanys();
        // 페이지 번호가 없으면 기본값 1 사용
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        // 한 페이지당 최대 15개 데이터
        int limit = 15;
        int startIndex = (pageNumber - 1) * limit;

        // 전체 리스트의 크기 체크
        List<MediaCompany> limitedMediaCompanys;
        if (startIndex < mediaCompanyList.size()) {
            int endIndex = Math.min(startIndex + limit, mediaCompanyList.size());
            limitedMediaCompanys =  mediaCompanyList.subList(startIndex, endIndex);
        } else {
            limitedMediaCompanys = new ArrayList<>(); // 페이지 번호가 범위를 벗어난 경우 빈 리스트
        }

        int totalPages = (int) Math.ceil((double) mediaCompanyList.size() / limit);
        int startPage = ((pageNumber - 1) / limit) * limit + 1; // 현재 페이지 그룹의 시작 페이지
        int endPage = Math.min(startPage + limit - 1, totalPages); // 현재 페이지 그룹의 끝 페이지
        model.addAttribute("mediaCompanyList", limitedMediaCompanys);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", (int) Math.ceil((double) mediaCompanyList.size() / limit));
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "affiliateList";
    }

    @GetMapping("/affiliateWrite")
    public String affiliateWrite(HttpSession session,Model model){
        Member sessionMember = (Member) session.getAttribute("member");
        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료
        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }
        List<Server> server = serverService.getServers();

        model.addAttribute("servers",server);
        return "affiliateWrite";
    }

    @GetMapping("/affiliateWrite/{idx}")
    public String affiliateEdit(HttpSession session,@PathVariable(required = false,value = "idx") int idx,Model model){

        Member sessionMember = (Member) session.getAttribute("member");
        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료
        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }

        MediaCompany mediaCompany = mediaCompanyService.getMediaCompany(idx);
        List<Server> server = serverService.getServers();
        if(mediaCompany==null)
            return "error/404";
        model.addAttribute("affiliate", mediaCompany);
        model.addAttribute("servers",server);
        return "affiliateWrite";
    }
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<String> handleSQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException ex) {
        if (ex.getMessage().contains("Duplicate entry")) {
            return new ResponseEntity<>("아이디가 중복되었습니다.", HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>("데이터베이스 오류가 발생했습니다. 다시 시도하거나 관리자에게 문의해주세요", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //////////////////////////////////////
    //////////////////////////////////////
    //////////////////////////////////////


    @GetMapping("/affiliateProfile/{idx}")
    public String affiliateProfile(HttpSession session,@PathVariable(required = true,value = "idx") int idx,Model model){

        Member sessionMember = (Member) session.getAttribute("member");
        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료
        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }

        MediaCompany mediaCompany = mediaCompanyService.getMediaCompany(idx);
        List<Server> server = serverService.getServers();
        if(mediaCompany==null)
            return "error/404";
        model.addAttribute("affiliate", mediaCompany);
        model.addAttribute("servers",server);
        return "affiliateProfile";
    }


    @Operation(summary = "관리자 정보 수정", description = "관리자 정보를 수정합니다")
    @Parameter(name = "idx", description = "수정할 관리자의 IDX")
    @PostMapping("/affiliateProfile/edit/{idx}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 수정됨"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "404", description = "회원정보 조회 실패")

    })
    public ResponseEntity<MediaCompany> affiliateProfileEdit(HttpSession session, @PathVariable("idx") int idx, @RequestBody MediaCompanyProfileEditDto dto, HttpServletResponse response){
        Member sessionMember= (Member) session.getAttribute("member");
        if(sessionMember == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } // 세션만료

        Member member =memberRepository.findById( sessionMember.getId());
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }//데이터 없음

        if(member.getAuthMediacompany()== Auth.READ) // 읽기 권한만 존재할경우
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        MediaCompany edited = mediaCompanyService.affiliateProfileEdit(idx,dto);
        if(edited == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(edited);
    }



    @GetMapping({"/{aidx}/affiliateQuizDaily","/{aidx}/affiliateQuizDaily/","/{aidx}/affiliateQuizDaily/{pageNumber}"})
    public String affiliateQuizDaily(HttpSession session, @PathVariable(required = true, value = "aidx") int aidx, @PathVariable(required = false, value = "pageNumber") Integer pageNumber, Model model) {
        Member sessionMember = (Member) session.getAttribute("member");

        MediaCompany mediaCompany = mediaCompanyService.getMediaCompany(aidx);
        if (mediaCompany == null) {
            return "error/404";
        }

        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료
        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }

        MediaCompany target = mediaCompanyService.getMediaCompany(aidx);
        List<AnswerMsnDailyStat> answerMsnDailyStats = answerMsnDailyService.findByMediaCompany(aidx);
        Collections.reverse(answerMsnDailyStats);
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        int limit = 20;
        int startIndex = (pageNumber - 1) * limit;

        List<AnswerMsnDailyStat> limitedAnswerMsnDailyStats;
        if (startIndex < answerMsnDailyStats.size()) {
            int endIndex = Math.min(startIndex + limit, answerMsnDailyStats.size());
            limitedAnswerMsnDailyStats = answerMsnDailyStats.subList(startIndex, endIndex);
        } else {
            limitedAnswerMsnDailyStats = new ArrayList<>(); // 페이지 번호가 범위를 벗어난 경우 빈 리스트
        }
        // 전체 페이지 수 계산
        int totalPages = (int) Math.ceil((double) answerMsnDailyStats.size() / limit);
        int startPage = ((pageNumber - 1) / limit) * limit + 1; // 현재 페이지 그룹의 시작 페이지
        int endPage = Math.min(startPage + limit - 1, totalPages); // 현재 페이지 그룹의 끝 페이지

        model.addAttribute("affiliate", mediaCompany);
        model.addAttribute("answerMsnDailyStats", limitedAnswerMsnDailyStats);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "affiliateQuizDaily";
    }


    @Operation(summary = "매체사-정답미션데일리 통계 검색", description = "매체사-정답미션 데일리 통계를 검색합니다")
    @PostMapping({"/{aidx}/affiliateQuizDaily/search","/{aidx}/affiliateQuizDaily/search/","/{aidx}/affiliateQuizDaily/search/{pageNumber}"})
    @ResponseBody
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
            @ApiResponse(responseCode = "500", description = "검색 중 예기치않은 오류발생")
    })
    public Map<String, Object> searchAffiliateQuizDaily(HttpSession session, @PathVariable(required = true, value = "aidx") int aidx, @PathVariable(required = false, value = "pageNumber") Integer pageNumber, Model model, @RequestBody MediaCompanyQuizDailySearchDto dto) {
        Member sessionMember = (Member) session.getAttribute("member");

        MediaCompany mediaCompany = mediaCompanyService.getMediaCompany(aidx);
        Map<String, Object> response = new HashMap<>();
        if (mediaCompany == null) {
            response.put("error", "404"); // 멤버가 없는 경우
            return response;
        }

        if (sessionMember == null) {
            response.put("error", "403"); // 멤버가 없는 경우
            return response;
        }

        List<AnswerMsnDailyStat> target = answerMsnDailyService.findByMediaCompany(aidx);

        List<AnswerMsnDailyStat> answerMsnDailyStats = answerMsnDailyService.searchAnswerMsnDailyByAffiliate(target,dto);
        Collections.reverse(answerMsnDailyStats);
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        int limit = 20;
        int startIndex = (pageNumber - 1) * limit;

        List<AnswerMsnDailyStat> limitedAnswerMsnDailyStats;
        if (startIndex < answerMsnDailyStats.size()) {
            int endIndex = Math.min(startIndex + limit, answerMsnDailyStats.size());
            limitedAnswerMsnDailyStats = answerMsnDailyStats.subList(startIndex, endIndex);
        } else {
            limitedAnswerMsnDailyStats = new ArrayList<>(); // 페이지 번호가 범위를 벗어난 경우 빈 리스트
        }
        // 전체 페이지 수 계산
        int totalPages = (int) Math.ceil((double) answerMsnDailyStats.size() / limit);
        int startPage = ((pageNumber - 1) / limit) * limit + 1; // 현재 페이지 그룹의 시작 페이지
        int endPage = Math.min(startPage + limit - 1, totalPages); // 현재 페이지 그룹의 끝 페이지


        response.put("affiliate", mediaCompany);
        response.put("answerMsnDailyStats", limitedAnswerMsnDailyStats);
        response.put("currentPage", pageNumber);
        response.put("aidx", aidx);
        response.put("startPage", startPage);
        response.put("endPage", endPage);

        return response;
    }

    @Operation(summary = "에러 방지", description = "검색 재진입시 원래 페이지로 리턴 ")
    @GetMapping("/{aidx}/affiliateQuizDaily/search/{pageNumber}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
    })
    public String  searchAffiliateQuizDaily_return(@PathVariable(required = true, value = "aidx") int aidx,@PathVariable(required = false,value = "pageNumber") Integer pageNumber,HttpSession session ,Model model){
        Member sessionMember = (Member) session.getAttribute("member");

        MediaCompany mediaCompany = mediaCompanyService.getMediaCompany(aidx);
        if (mediaCompany == null) {
            return "error/404";
        }

        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료
        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }

        MediaCompany target = mediaCompanyService.getMediaCompany(aidx);
        List<AnswerMsnDailyStat> answerMsnDailyStats = answerMsnDailyService.findByMediaCompany(aidx);
        Collections.reverse(answerMsnDailyStats);
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        int limit = 20;
        int startIndex = (pageNumber - 1) * limit;

        List<AnswerMsnDailyStat> limitedAnswerMsnDailyStats;
        if (startIndex < answerMsnDailyStats.size()) {
            int endIndex = Math.min(startIndex + limit, answerMsnDailyStats.size());
            limitedAnswerMsnDailyStats = answerMsnDailyStats.subList(startIndex, endIndex);
        } else {
            limitedAnswerMsnDailyStats = new ArrayList<>(); // 페이지 번호가 범위를 벗어난 경우 빈 리스트
        }
        // 전체 페이지 수 계산
        int totalPages = (int) Math.ceil((double) answerMsnDailyStats.size() / limit);
        int startPage = ((pageNumber - 1) / limit) * limit + 1; // 현재 페이지 그룹의 시작 페이지
        int endPage = Math.min(startPage + limit - 1, totalPages); // 현재 페이지 그룹의 끝 페이지

        model.addAttribute("affiliate", mediaCompany);
        model.addAttribute("answerMsnDailyStats", limitedAnswerMsnDailyStats);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "affiliateQuizDaily";
    }


    @Operation(summary = "매체사-미션 리포트 엑셀 다운로드", description = "매체사-정답미션 데일리 통계 엑셀파일을 다운로드합니다")
    @GetMapping("/{aidx}/affiliateQuizDaily/excel/download")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "500", description = "예기치않은 오류발생")
    })
    public ResponseEntity<Void> quizDailyExcelDownload(@PathVariable(required = true, value = "aidx") int aidx,HttpServletResponse response)throws IOException {

        try (Workbook wb = new XSSFWorkbook()) {
            List<AnswerMsnDailyStat> answerMsnDailyStats = answerMsnDailyService.findByMediaCompany(aidx);
            String companyName = mediaCompanyService.getMediaCompany(aidx).getCompanyName();
            Collections.reverse(answerMsnDailyStats);
            Sheet sheet = answerMsnDailyService.excelDownload(answerMsnDailyStats,wb);
            if(sheet !=null) {
                String name = "정답미션 데일리 리포트"+companyName+".xlsx";
                String fileName = URLEncoder.encode(name, StandardCharsets.UTF_8);
                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
                wb.write(response.getOutputStream());
                response.flushBuffer();
                return ResponseEntity.status(HttpStatus.OK).build();
            }
            else
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }




    @GetMapping({"/{aidx}/affiliateQuizSum","/{aidx}/affiliateQuizSum/","/{aidx}/affiliateQuizSum/{pageNumber}"})
    public String affiliateQuizSum(HttpSession session, @PathVariable(required = true, value = "aidx") int aidx, @PathVariable(required = false, value = "pageNumber") Integer pageNumber, Model model) {
        Member sessionMember = (Member) session.getAttribute("member");
        MediaCompany mediaCompany = mediaCompanyService.getMediaCompany(aidx);

        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료

        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }

        List<AnswerMsnSumStat> answerMsnSumStats = answerMsnSumStatService.getAnswerMsnSumStatsByAffiliate(aidx);
        Collections.reverse(answerMsnSumStats);
        if (answerMsnSumStats.size() > 30) {
            answerMsnSumStats = answerMsnSumStats.subList(0, 30);
        }


        int totalLandingCount = answerMsnSumStats.stream().mapToInt(AnswerMsnSumStat::getLandingCnt).sum();  // 랜딩카운트 합
        int totalPartCount =  answerMsnSumStats.stream().mapToInt(AnswerMsnSumStat::getPartCnt).sum();  // 참여카운트 합

        model.addAttribute("affiliate", mediaCompany);
        model.addAttribute("answerMsnSumStats", answerMsnSumStats);
        model.addAttribute("totalLandingCount",totalLandingCount);
        model.addAttribute("totalPartCount",totalPartCount);
        return "affiliateQuizSum";
    }


    @Operation(summary = "매체사-정답미션데일리 통계 검색", description = "매체사-정답미션 데일리 통계를 검색합니다")
    @PostMapping({"/{aidx}/affiliateQuizSum/search","/{aidx}/affiliateQuizSum/search/","/{aidx}/affiliateQuizSum/search/{pageNumber}"})
    @ResponseBody
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
            @ApiResponse(responseCode = "500", description = "검색 중 예기치않은 오류발생")
    })
    public Map<String, Object> searchAffiliateQuizSum(HttpSession session, @PathVariable(required = true, value = "aidx") int aidx, @PathVariable(required = false, value = "pageNumber") Integer pageNumber, Model model, @RequestBody MediaCompanyQuizSumSearchDto dto) {
        Member sessionMember = (Member) session.getAttribute("member");

        MediaCompany mediaCompany = mediaCompanyService.getMediaCompany(aidx);
        Map<String, Object> response = new HashMap<>();
        if (mediaCompany == null) {
            response.put("error", "404"); // 멤버가 없는 경우
            return response;
        }

        if (sessionMember == null) {
            response.put("error", "403"); // 멤버가 없는 경우
            return response;
        }

        List<AnswerMsnSumStat> target = answerMsnSumStatService.getAnswerMsnSumStatsByAffiliate(aidx);

        List<AnswerMsnSumStat> answerMsnSumStats = answerMsnSumStatService.searchByAffiliate(target,dto);
        Collections.reverse(answerMsnSumStats);
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        int limit = 30;
        int startIndex = (pageNumber - 1) * limit;

        List<AnswerMsnSumStat> limitedAnswerMsnSumStats;
        if (startIndex < answerMsnSumStats.size()) {
            int endIndex = Math.min(startIndex + limit, answerMsnSumStats.size());
            limitedAnswerMsnSumStats = answerMsnSumStats.subList(startIndex, endIndex);
        } else {
            limitedAnswerMsnSumStats = new ArrayList<>(); // 페이지 번호가 범위를 벗어난 경우 빈 리스트
        }
        // 전체 페이지 수 계산
        int totalPages = (int) Math.ceil((double) answerMsnSumStats.size() / limit);
        int startPage = ((pageNumber - 1) / limit) * limit + 1; // 현재 페이지 그룹의 시작 페이지
        int endPage = Math.min(startPage + limit - 1, totalPages); // 현재 페이지 그룹의 끝 페이지

        int totalLandingCount = answerMsnSumStats.stream().mapToInt(AnswerMsnSumStat::getLandingCnt).sum();  // 랜딩카운트 합
        int totalPartCount =  answerMsnSumStats.stream().mapToInt(AnswerMsnSumStat::getPartCnt).sum();  // 참여카운트 합


        response.put("affiliate", mediaCompany);
        response.put("answerMsnSumStats", limitedAnswerMsnSumStats);
        response.put("currentPage", pageNumber);
        response.put("aidx", aidx);
        response.put("startPage", startPage);
        response.put("endPage", endPage);
        response.put("totalLandingCount",totalLandingCount);
        response.put("totalPartCount",totalPartCount);

        return response;
    }

    @Operation(summary = "에러 방지", description = "검색 재진입시 원래 페이지로 리턴 ")
    @GetMapping("/{aidx}/affiliateQuizSum/search/{pageNumber}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
    })
    public String searchAffiliateQuizSum_return(@PathVariable(required = true, value = "aidx") int aidx,@PathVariable(required = false,value = "pageNumber") Integer pageNumber,HttpSession session ,Model model){
        Member sessionMember = (Member) session.getAttribute("member");

        MediaCompany mediaCompany = mediaCompanyService.getMediaCompany(aidx);
        if (mediaCompany == null) {
            return "error/404";
        }

        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료
        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }

        MediaCompany target = mediaCompanyService.getMediaCompany(aidx);

        List<AnswerMsnSumStat> answerMsnSumStats = answerMsnSumStatService.getAnswerMsnSumStatsByAffiliate(aidx);
        Collections.reverse(answerMsnSumStats);


        Collections.reverse(answerMsnSumStats);
        if (answerMsnSumStats.size() > 30) {
            answerMsnSumStats = answerMsnSumStats.subList(0, 30);
        }


        int totalLandingCount = answerMsnSumStats.stream().mapToInt(AnswerMsnSumStat::getLandingCnt).sum();  // 랜딩카운트 합
        int totalPartCount =  answerMsnSumStats.stream().mapToInt(AnswerMsnSumStat::getPartCnt).sum();  // 참여카운트 합

        model.addAttribute("affiliate", mediaCompany);
        model.addAttribute("answerMsnSumStats", answerMsnSumStats);
        model.addAttribute("totalLandingCount",totalLandingCount);
        model.addAttribute("totalPartCount",totalPartCount);
        return "affiliateQuizSum";
    }


    @Operation(summary = "매체사-미션 리포트 엑셀 다운로드", description = "매체사-정답미션 데일리 통계 엑셀파일을 다운로드합니다")
    @GetMapping("/{aidx}/affiliateQuizSum/excel/download")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "500", description = "예기치않은 오류발생")
    })
    public ResponseEntity<Void> quizSumExcelDownload(@PathVariable(required = true, value = "aidx") int aidx,HttpServletResponse response)throws IOException {
        try (Workbook wb = new XSSFWorkbook()) {
            LocalDate currentDate = LocalDate.now();
            LocalDate past = currentDate.minusMonths(1);
            List<AnswerMsnSumStat> list = answerMsnSumStatService.getAnswerMsnSumStatsMonthByAffiliate(currentDate,past,aidx);
            Collections.reverse(list);
            int totalLandingCount = list.stream().mapToInt(AnswerMsnSumStat::getLandingCnt).sum();  // 랜딩카운트 합
            int totalPartCount =  list.stream().mapToInt(AnswerMsnSumStat::getPartCnt).sum();  // 참여카운트
            Sheet sheet = answerMsnSumStatService.excelDownloadCurrent(list,wb,totalLandingCount,totalPartCount);
            if(sheet !=null) {
                String name = "현재 리스트 소진량(정답)" +mediaCompanyService.getMediaCompany(aidx).getCompanyName() +".xlsx";
                String fileName = URLEncoder.encode(name, StandardCharsets.UTF_8);
                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
                wb.write(response.getOutputStream());
                response.flushBuffer();
                return ResponseEntity.status(HttpStatus.OK).build();
            }
            else
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }





    @GetMapping({"/{aidx}/affiliateQuizCurrent","/{aidx}/affiliateQuizCurrent/","/{aidx}/affiliateQuizCurrent/{pageNumber}"})
    public String affiliateQuizCurrentList(HttpSession session, @PathVariable(required = true, value = "aidx") int aidx, @PathVariable(required = false, value = "pageNumber") Integer pageNumber, Model model) {
        Member sessionMember = (Member) session.getAttribute("member");
        MediaCompany mediaCompany = mediaCompanyService.getMediaCompany(aidx);

        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료
        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }

        LocalDateTime now = LocalDateTime.now();
        List<AnswerMsn> answerMsns = answerMsnService.findByCurrentListAffiliate(now,aidx);

        // 페이지 번호가 없으면 기본값 1 사용
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }

        // 한 페이지당 최대 15개 데이터
        int limit = 15;
        int startIndex = (pageNumber - 1) * limit;

        // 전체 리스트의 크기 체크
        List<AnswerMsn> limitedAnswerMsns;
        if (startIndex < answerMsns.size()) {
            int endIndex = Math.min(startIndex + limit, answerMsns.size());
            limitedAnswerMsns = answerMsns.subList(startIndex, endIndex);
        } else {
            limitedAnswerMsns = new ArrayList<>(); // 페이지 번호가 범위를 벗어난 경우 빈 리스트
        }

        int totalPages = (int) Math.ceil((double) answerMsns.size() / limit);
        int startPage = ((pageNumber - 1) / limit) * limit + 1; // 현재 페이지 그룹의 시작 페이지
        int endPage = Math.min(startPage + limit - 1, totalPages); // 현재 페이지 그룹의 끝 페이지

        model.addAttribute("affiliate", mediaCompany);
        model.addAttribute("aidx", aidx);
        model.addAttribute("answerMsns",limitedAnswerMsns);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "affiliateQuizCurrent";
    }


    @Operation(summary = "매체사-정답미션 현재 리스트 검색", description = "매체사-정답미션 현재 리스트를 검색합니다")
    @PostMapping({"/{aidx}/affiliateQuizCurrent/search","/{aidx}/affiliateQuizCurrent/search/","/{aidx}/affiliateQuizCurrent/search/{pageNumber}"})
    @ResponseBody
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
            @ApiResponse(responseCode = "500", description = "검색 중 예기치않은 오류발생")
    })
    public Map<String, Object> searchAffiliateQuizCurrent(HttpSession session, @PathVariable(required = true, value = "aidx") int aidx, @PathVariable(required = false, value = "pageNumber") Integer pageNumber, Model model, @RequestBody MediaCompanyQuizCurrentSearchDto dto) {
        Member sessionMember = (Member) session.getAttribute("member");

        MediaCompany mediaCompany = mediaCompanyService.getMediaCompany(aidx);
        Map<String, Object> response = new HashMap<>();
        if (mediaCompany == null) {
            response.put("error", "404"); // 멤버가 없는 경우
            return response;
        }

        if (sessionMember == null) {
            response.put("error", "403"); // 멤버가 없는 경우
            return response;
        }

        List<AnswerMsn> target = answerMsnService.findByCurrentListAffiliate(LocalDateTime.now(),aidx);

        List<AnswerMsn> answerMsnSumStats = answerMsnService.searchAnswerMsnCurrentByAffiliate(target,dto);
        Collections.reverse(answerMsnSumStats);
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        int limit = 30;
        int startIndex = (pageNumber - 1) * limit;

        List<AnswerMsn> limitedAnswerMsn;
        if (startIndex < answerMsnSumStats.size()) {
            int endIndex = Math.min(startIndex + limit, answerMsnSumStats.size());
            limitedAnswerMsn = answerMsnSumStats.subList(startIndex, endIndex);
        } else {
            limitedAnswerMsn = new ArrayList<>(); // 페이지 번호가 범위를 벗어난 경우 빈 리스트
        }
        // 전체 페이지 수 계산
        int totalPages = (int) Math.ceil((double) answerMsnSumStats.size() / limit);
        int startPage = ((pageNumber - 1) / limit) * limit + 1; // 현재 페이지 그룹의 시작 페이지
        int endPage = Math.min(startPage + limit - 1, totalPages); // 현재 페이지 그룹의 끝 페이지

        response.put("affiliate", mediaCompany);
        response.put("answerMsns", limitedAnswerMsn);
        response.put("totalPages", totalPages);
        response.put("currentPage", pageNumber);
        response.put("aidx", aidx);
        response.put("startPage", startPage);
        response.put("endPage", endPage);
        response.put("totalPages", totalPages);

        return response;
    }

    @Operation(summary = "에러 방지", description = "검색 재진입시 원래 페이지로 리턴 ")
    @GetMapping("/{aidx}/affiliateQuizCurrent/search/{pageNumber}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
    })
    public String searchAffiliateQuizCurrent_return(@PathVariable(required = true, value = "aidx") int aidx,@PathVariable(required = false,value = "pageNumber") Integer pageNumber,HttpSession session ,Model model){
        Member sessionMember = (Member) session.getAttribute("member");
        MediaCompany mediaCompany = mediaCompanyService.getMediaCompany(aidx);

        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료
        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }

        LocalDateTime now = LocalDateTime.now();
        List<AnswerMsn> answerMsns = answerMsnService.findByCurrentListAffiliate(now,aidx);

        // 페이지 번호가 없으면 기본값 1 사용
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }

        // 한 페이지당 최대 15개 데이터
        int limit = 15;
        int startIndex = (pageNumber - 1) * limit;

        // 전체 리스트의 크기 체크
        List<AnswerMsn> limitedAnswerMsns;
        if (startIndex < answerMsns.size()) {
            int endIndex = Math.min(startIndex + limit, answerMsns.size());
            limitedAnswerMsns = answerMsns.subList(startIndex, endIndex);
        } else {
            limitedAnswerMsns = new ArrayList<>(); // 페이지 번호가 범위를 벗어난 경우 빈 리스트
        }

        int totalPages = (int) Math.ceil((double) answerMsns.size() / limit);
        int startPage = ((pageNumber - 1) / limit) * limit + 1; // 현재 페이지 그룹의 시작 페이지
        int endPage = Math.min(startPage + limit - 1, totalPages); // 현재 페이지 그룹의 끝 페이지

        model.addAttribute("answerMsns",limitedAnswerMsns);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "affiliateQuizCurrent";
    }
    //////////////////////////////////////
    //////////////////////////////////////
    //////////////////////////////////////

    @GetMapping({"/{aidx}/affiliateSightseeingDaily","/{aidx}/affiliateSightseeingDaily/","/{aidx}/affiliateSightseeingDaily/{pageNumber}"})
    public String affiliateSightseeingDaily(HttpSession session, @PathVariable(required = true, value = "aidx") int aidx, @PathVariable(required = false, value = "pageNumber") Integer pageNumber, Model model) {
        Member sessionMember = (Member) session.getAttribute("member");

        MediaCompany mediaCompany = mediaCompanyService.getMediaCompany(aidx);
        if (mediaCompany == null) {
            return "error/404";
        }

        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료
        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }

        MediaCompany target = mediaCompanyService.getMediaCompany(aidx);
        List<SaveMsnDailyStat> saveMsnDailyStats = saveMsnDailyService.findByMediaCompany(aidx);
        Collections.reverse(saveMsnDailyStats);
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        int limit = 20;
        int startIndex = (pageNumber - 1) * limit;

        List<SaveMsnDailyStat> limitedSaveMsnDailyStats;
        if (startIndex < saveMsnDailyStats.size()) {
            int endIndex = Math.min(startIndex + limit, saveMsnDailyStats.size());
            limitedSaveMsnDailyStats = saveMsnDailyStats.subList(startIndex, endIndex);
        } else {
            limitedSaveMsnDailyStats = new ArrayList<>(); // 페이지 번호가 범위를 벗어난 경우 빈 리스트
        }
        // 전체 페이지 수 계산
        int totalPages = (int) Math.ceil((double) saveMsnDailyStats.size() / limit);
        int startPage = ((pageNumber - 1) / limit) * limit + 1; // 현재 페이지 그룹의 시작 페이지
        int endPage = Math.min(startPage + limit - 1, totalPages); // 현재 페이지 그룹의 끝 페이지

        model.addAttribute("affiliate", mediaCompany);
        model.addAttribute("saveMsnDailyStats", limitedSaveMsnDailyStats);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "affiliateSightseeingDaily";
    }


    @Operation(summary = "매체사-저장미션데일리 통계 검색", description = "매체사-저장미션 데일리 통계를 검색합니다")
    @PostMapping({"/{aidx}/affiliateSightseeingDaily/search","/{aidx}/affiliateSightseeingDaily/search/","/{aidx}/affiliateSightseeingDaily/search/{pageNumber}"})
    @ResponseBody
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
            @ApiResponse(responseCode = "500", description = "검색 중 예기치않은 오류발생")
    })
    public Map<String, Object> searchAffiliateSightseeingDaily(HttpSession session, @PathVariable(required = true, value = "aidx") int aidx, @PathVariable(required = false, value = "pageNumber") Integer pageNumber, Model model, @RequestBody MediaCompanySightseeingDailySearchDto dto) {
        Member sessionMember = (Member) session.getAttribute("member");

        MediaCompany mediaCompany = mediaCompanyService.getMediaCompany(aidx);
        Map<String, Object> response = new HashMap<>();
        if (mediaCompany == null) {
            response.put("error", "404"); // 멤버가 없는 경우
            return response;
        }

        if (sessionMember == null) {
            response.put("error", "403"); // 멤버가 없는 경우
            return response;
        }

        List<SaveMsnDailyStat> target = saveMsnDailyService.findByMediaCompany(aidx);

        List<SaveMsnDailyStat> saveMsnDailyStats = saveMsnDailyService.searchSaveMsnDailyByAffiliate(target,dto);
        Collections.reverse(saveMsnDailyStats);
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        int limit = 20;
        int startIndex = (pageNumber - 1) * limit;

        List<SaveMsnDailyStat> limitedSaveMsnDailyStats;
        if (startIndex < saveMsnDailyStats.size()) {
            int endIndex = Math.min(startIndex + limit, saveMsnDailyStats.size());
            limitedSaveMsnDailyStats = saveMsnDailyStats.subList(startIndex, endIndex);
        } else {
            limitedSaveMsnDailyStats = new ArrayList<>(); // 페이지 번호가 범위를 벗어난 경우 빈 리스트
        }
        // 전체 페이지 수 계산
        int totalPages = (int) Math.ceil((double) saveMsnDailyStats.size() / limit);
        int startPage = ((pageNumber - 1) / limit) * limit + 1; // 현재 페이지 그룹의 시작 페이지
        int endPage = Math.min(startPage + limit - 1, totalPages); // 현재 페이지 그룹의 끝 페이지


        response.put("affiliate", mediaCompany);
        response.put("saveMsnDailyStats", limitedSaveMsnDailyStats);
        response.put("currentPage", pageNumber);
        response.put("aidx", aidx);
        response.put("startPage", startPage);
        response.put("totalPages", totalPages);
        response.put("endPage", endPage);

        return response;
    }

    @Operation(summary = "에러 방지", description = "검색 재진입시 원래 페이지로 리턴 ")
    @GetMapping("/{aidx}/affiliateSightseeingDaily/search/{pageNumber}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
    })
    public String  searchAffiliateSightseeingDaily_return(@PathVariable(required = true, value = "aidx") int aidx,@PathVariable(required = false,value = "pageNumber") Integer pageNumber,HttpSession session ,Model model){
        Member sessionMember = (Member) session.getAttribute("member");

        MediaCompany mediaCompany = mediaCompanyService.getMediaCompany(aidx);
        if (mediaCompany == null) {
            return "error/404";
        }

        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료
        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }

        MediaCompany target = mediaCompanyService.getMediaCompany(aidx);
        List<SaveMsnDailyStat> saveMsnDailyStats = saveMsnDailyService.findByMediaCompany(aidx);
        Collections.reverse(saveMsnDailyStats);
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        int limit = 20;
        int startIndex = (pageNumber - 1) * limit;

        List<SaveMsnDailyStat> limitedSaveMsnDailyStats;
        if (startIndex < saveMsnDailyStats.size()) {
            int endIndex = Math.min(startIndex + limit, saveMsnDailyStats.size());
            limitedSaveMsnDailyStats = saveMsnDailyStats.subList(startIndex, endIndex);
        } else {
            limitedSaveMsnDailyStats = new ArrayList<>(); // 페이지 번호가 범위를 벗어난 경우 빈 리스트
        }
        // 전체 페이지 수 계산
        int totalPages = (int) Math.ceil((double) saveMsnDailyStats.size() / limit);
        int startPage = ((pageNumber - 1) / limit) * limit + 1; // 현재 페이지 그룹의 시작 페이지
        int endPage = Math.min(startPage + limit - 1, totalPages); // 현재 페이지 그룹의 끝 페이지

        model.addAttribute("affiliate", mediaCompany);
        model.addAttribute("saveMsnDailyStats", limitedSaveMsnDailyStats);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "affiliateSightseeingDaily";
    }


    @Operation(summary = "매체사-미션 리포트 엑셀 다운로드", description = "매체사-저장미션 데일리 통계 엑셀파일을 다운로드합니다")
    @GetMapping("/{aidx}/affiliateSightseeingDaily/excel/download")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "500", description = "예기치않은 오류발생")
    })
    public ResponseEntity<Void> sightseeingDailyExcelDownload(@PathVariable(required = true, value = "aidx") int aidx,HttpServletResponse response)throws IOException {

        try (Workbook wb = new XSSFWorkbook()) {
            List<SaveMsnDailyStat> saveMsnDailyStats = saveMsnDailyService.findByMediaCompany(aidx);
            String companyName = mediaCompanyService.getMediaCompany(aidx).getCompanyName();
            Collections.reverse(saveMsnDailyStats);
            Sheet sheet = saveMsnDailyService.excelDownload(saveMsnDailyStats,wb);
            if(sheet !=null) {
                String name = "저장미션 데일리 리포트"+companyName+".xlsx";
                String fileName = URLEncoder.encode(name, StandardCharsets.UTF_8);
                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
                wb.write(response.getOutputStream());
                response.flushBuffer();
                return ResponseEntity.status(HttpStatus.OK).build();
            }
            else
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }




    @GetMapping({"/{aidx}/affiliateSightseeingSum","/{aidx}/affiliateSightseeingSum/","/{aidx}/affiliateSightseeingSum/{pageNumber}"})
    public String affiliateSightseeingSum(HttpSession session, @PathVariable(required = true, value = "aidx") int aidx, @PathVariable(required = false, value = "pageNumber") Integer pageNumber, Model model) {
        Member sessionMember = (Member) session.getAttribute("member");
        MediaCompany mediaCompany = mediaCompanyService.getMediaCompany(aidx);

        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료

        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }

        List<SaveMsnSumStat> saveMsnSumStats = saveMsnSumStatService.getSaveMsnSumStatsByAffiliate(aidx);
        Collections.reverse(saveMsnSumStats);
        if (saveMsnSumStats.size() > 30) {
            saveMsnSumStats = saveMsnSumStats.subList(0, 30);
        }


        int totalLandingCount = saveMsnSumStats.stream().mapToInt(SaveMsnSumStat::getLandingCnt).sum();  // 랜딩카운트 합
        int totalPartCount =  saveMsnSumStats.stream().mapToInt(SaveMsnSumStat::getPartCnt).sum();  // 참여카운트 합

        model.addAttribute("affiliate", mediaCompany);
        model.addAttribute("searchMsnSumStats", saveMsnSumStats);
        model.addAttribute("totalLandingCount",totalLandingCount);
        model.addAttribute("totalPartCount",totalPartCount);
        return "affiliateSightseeingSum";
    }


    @Operation(summary = "매체사-저장미션데일리 통계 검색", description = "매체사-저장미션 데일리 통계를 검색합니다")
    @PostMapping({"/{aidx}/affiliateSightseeingSum/search","/{aidx}/affiliateSightseeingSum/search/","/{aidx}/affiliateSightseeingSum/search/{pageNumber}"})
    @ResponseBody
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
            @ApiResponse(responseCode = "500", description = "검색 중 예기치않은 오류발생")
    })
    public Map<String, Object> searchAffiliateSightseeingSum(HttpSession session, @PathVariable(required = true, value = "aidx") int aidx, @PathVariable(required = false, value = "pageNumber") Integer pageNumber, Model model, @RequestBody MediaCompanySightseeingSumSearchDto dto) {
        Member sessionMember = (Member) session.getAttribute("member");

        MediaCompany mediaCompany = mediaCompanyService.getMediaCompany(aidx);
        Map<String, Object> response = new HashMap<>();
        if (mediaCompany == null) {
            response.put("error", "404"); // 멤버가 없는 경우
            return response;
        }

        if (sessionMember == null) {
            response.put("error", "403"); // 멤버가 없는 경우
            return response;
        }

        List<SaveMsnSumStat> target = saveMsnSumStatService.getSaveMsnSumStatsByAffiliate(aidx);

        List<SaveMsnSumStat> saveMsnSumStats = saveMsnSumStatService.searchByAffiliate(target,dto);
        Collections.reverse(saveMsnSumStats);
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        int limit = 30;
        int startIndex = (pageNumber - 1) * limit;

        List<SaveMsnSumStat> limitedSaveMsnSumStats;
        if (startIndex < saveMsnSumStats.size()) {
            int endIndex = Math.min(startIndex + limit, saveMsnSumStats.size());
            limitedSaveMsnSumStats = saveMsnSumStats.subList(startIndex, endIndex);
        } else {
            limitedSaveMsnSumStats = new ArrayList<>(); // 페이지 번호가 범위를 벗어난 경우 빈 리스트
        }
        // 전체 페이지 수 계산
        int totalPages = (int) Math.ceil((double) saveMsnSumStats.size() / limit);
        int startPage = ((pageNumber - 1) / limit) * limit + 1; // 현재 페이지 그룹의 시작 페이지
        int endPage = Math.min(startPage + limit - 1, totalPages); // 현재 페이지 그룹의 끝 페이지

        int totalLandingCount = saveMsnSumStats.stream().mapToInt(SaveMsnSumStat::getLandingCnt).sum();  // 랜딩카운트 합
        int totalPartCount =  saveMsnSumStats.stream().mapToInt(SaveMsnSumStat::getPartCnt).sum();  // 참여카운트 합


        response.put("affiliate", mediaCompany);
        response.put("saveMsnSumStats", limitedSaveMsnSumStats);
        response.put("currentPage", pageNumber);
        response.put("aidx", aidx);
        response.put("startPage", startPage);
        response.put("endPage", endPage);
        response.put("totalPages", totalPages);
        response.put("totalLandingCount",totalLandingCount);
        response.put("totalPartCount",totalPartCount);

        return response;
    }

    @Operation(summary = "에러 방지", description = "검색 재진입시 원래 페이지로 리턴 ")
    @GetMapping("/{aidx}/affiliateSaveSum/search/{pageNumber}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
    })
    public String searchAffiliateSaveSum_return(@PathVariable(required = true, value = "aidx") int aidx,@PathVariable(required = false,value = "pageNumber") Integer pageNumber,HttpSession session ,Model model){
        Member sessionMember = (Member) session.getAttribute("member");
        MediaCompany mediaCompany = mediaCompanyService.getMediaCompany(aidx);

        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료

        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }

        List<SaveMsnSumStat> saveMsnSumStats = saveMsnSumStatService.getSaveMsnSumStatsByAffiliate(aidx);
        Collections.reverse(saveMsnSumStats);
        if (saveMsnSumStats.size() > 30) {
            saveMsnSumStats = saveMsnSumStats.subList(0, 30);
        }


        int totalLandingCount = saveMsnSumStats.stream().mapToInt(SaveMsnSumStat::getLandingCnt).sum();  // 랜딩카운트 합
        int totalPartCount =  saveMsnSumStats.stream().mapToInt(SaveMsnSumStat::getPartCnt).sum();  // 참여카운트 합

        model.addAttribute("affiliate", mediaCompany);
        model.addAttribute("saveMsnSumStats", saveMsnSumStats);
        model.addAttribute("totalLandingCount",totalLandingCount);
        model.addAttribute("totalPartCount",totalPartCount);
        return "affiliateSightseeingSum";
    }


    @Operation(summary = "매체사-미션 리포트 엑셀 다운로드", description = "매체사-저장미션 데일리 통계 엑셀파일을 다운로드합니다")
    @GetMapping("/{aidx}/affiliateSightseeingSum/excel/download")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "500", description = "예기치않은 오류발생")
    })
    public ResponseEntity<Void> sightseeingSumExcelDownload(@PathVariable(required = true, value = "aidx") int aidx,HttpServletResponse response)throws IOException {

        try (Workbook wb = new XSSFWorkbook()) {
            LocalDate currentDate = LocalDate.now();
            LocalDate past = currentDate.minusMonths(1);
            List<SaveMsnSumStat> list = saveMsnSumStatService.getSaveMsnSumStatsMonthByAffiliate(currentDate,past,aidx);
            Collections.reverse(list);
            int totalLandingCount = list.stream().mapToInt(SaveMsnSumStat::getLandingCnt).sum();  // 랜딩카운트 합
            int totalPartCount =  list.stream().mapToInt(SaveMsnSumStat::getPartCnt).sum();  // 참여카운트
            Sheet sheet = saveMsnSumStatService.excelDownloadCurrent(list,wb,totalLandingCount,totalPartCount);
            if(sheet !=null) {
                String name = "현재 리스트 소진량(저장)" +mediaCompanyService.getMediaCompany(aidx).getCompanyName() +".xlsx";
                String fileName = URLEncoder.encode(name, StandardCharsets.UTF_8);
                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
                wb.write(response.getOutputStream());
                response.flushBuffer();
                return ResponseEntity.status(HttpStatus.OK).build();
            }
            else
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }





    @GetMapping({"/{aidx}/affiliateSightseeingCurrent","/{aidx}/affiliateSightseeingCurrent/","/{aidx}/affiliateSightseeingCurrent/{pageNumber}"})
    public String affiliateSightseeingCurrentList(HttpSession session, @PathVariable(required = true, value = "aidx") int aidx, @PathVariable(required = false, value = "pageNumber") Integer pageNumber, Model model) {
        Member sessionMember = (Member) session.getAttribute("member");
        MediaCompany mediaCompany = mediaCompanyService.getMediaCompany(aidx);

        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료
        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }

        LocalDateTime now = LocalDateTime.now();
        List<SaveMsn> saveMsns = saveMsnService.findByCurrentListAffiliate(now,aidx);

        // 페이지 번호가 없으면 기본값 1 사용
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }

        // 한 페이지당 최대 15개 데이터
        int limit = 15;
        int startIndex = (pageNumber - 1) * limit;

        // 전체 리스트의 크기 체크
        List<SaveMsn> limitedSaveMsns;
        if (startIndex < saveMsns.size()) {
            int endIndex = Math.min(startIndex + limit, saveMsns.size());
            limitedSaveMsns = saveMsns.subList(startIndex, endIndex);
        } else {
            limitedSaveMsns = new ArrayList<>(); // 페이지 번호가 범위를 벗어난 경우 빈 리스트
        }

        int totalPages = (int) Math.ceil((double) saveMsns.size() / limit);
        int startPage = ((pageNumber - 1) / limit) * limit + 1; // 현재 페이지 그룹의 시작 페이지
        int endPage = Math.min(startPage + limit - 1, totalPages); // 현재 페이지 그룹의 끝 페이지

        model.addAttribute("affiliate", mediaCompany);
        model.addAttribute("aidx", aidx);
        model.addAttribute("saveMsns",limitedSaveMsns);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "affiliateSightseeingCurrent";
    }


    @Operation(summary = "매체사-저장미션 현재 리스트 검색", description = "매체사-저장미션 현재 리스트를 검색합니다")
    @PostMapping({"/{aidx}/affiliateSightseeingCurrent/search","/{aidx}/affiliateSightseeingCurrent/search/","/{aidx}/affiliateSightseeingCurrent/search/{pageNumber}"})
    @ResponseBody
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
            @ApiResponse(responseCode = "500", description = "검색 중 예기치않은 오류발생")
    })
    public Map<String, Object> searchAffiliateSightseeingCurrent(HttpSession session, @PathVariable(required = true, value = "aidx") int aidx, @PathVariable(required = false, value = "pageNumber") Integer pageNumber, Model model, @RequestBody MediaCompanySightseeingCurrentSearchDto dto) {
        Member sessionMember = (Member) session.getAttribute("member");

        MediaCompany mediaCompany = mediaCompanyService.getMediaCompany(aidx);
        Map<String, Object> response = new HashMap<>();
        if (mediaCompany == null) {
            response.put("error", "404"); // 멤버가 없는 경우
            return response;
        }

        if (sessionMember == null) {
            response.put("error", "403"); // 멤버가 없는 경우
            return response;
        }

        List<SaveMsn> target = saveMsnService.findByCurrentListAffiliate(LocalDateTime.now(),aidx);

        List<SaveMsn> saveMsnSumStats = saveMsnService.searchSaveMsnCurrentByAffiliate(target,dto);
        Collections.reverse(saveMsnSumStats);
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        int limit = 30;
        int startIndex = (pageNumber - 1) * limit;

        List<SaveMsn> limitedSaveMsn;
        if (startIndex < saveMsnSumStats.size()) {
            int endIndex = Math.min(startIndex + limit, saveMsnSumStats.size());
            limitedSaveMsn = saveMsnSumStats.subList(startIndex, endIndex);
        } else {
            limitedSaveMsn = new ArrayList<>(); // 페이지 번호가 범위를 벗어난 경우 빈 리스트
        }
        // 전체 페이지 수 계산
        int totalPages = (int) Math.ceil((double) saveMsnSumStats.size() / limit);
        int startPage = ((pageNumber - 1) / limit) * limit + 1; // 현재 페이지 그룹의 시작 페이지
        int endPage = Math.min(startPage + limit - 1, totalPages); // 현재 페이지 그룹의 끝 페이지

        response.put("affiliate", mediaCompany);
        response.put("totalPages", totalPages);
        response.put("saveMsns", limitedSaveMsn);
        response.put("currentPage", pageNumber);
        response.put("aidx", aidx);
        response.put("startPage", startPage);
        response.put("endPage", endPage);


        return response;
    }

    @Operation(summary = "에러 방지", description = "검색 재진입시 원래 페이지로 리턴 ")
    @GetMapping("/{aidx}/affiliateSightseeingCurrent/search/{pageNumber}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
    })
    public String searchAffiliateSightseeingCurrent_return(@PathVariable(required = true, value = "aidx") int aidx,@PathVariable(required = false,value = "pageNumber") Integer pageNumber,HttpSession session ,Model model){
        Member sessionMember = (Member) session.getAttribute("member");
        MediaCompany mediaCompany = mediaCompanyService.getMediaCompany(aidx);

        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료
        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }

        LocalDateTime now = LocalDateTime.now();
        List<SaveMsn> saveMsns = saveMsnService.findByCurrentListAffiliate(now,aidx);

        // 페이지 번호가 없으면 기본값 1 사용
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }

        // 한 페이지당 최대 15개 데이터
        int limit = 15;
        int startIndex = (pageNumber - 1) * limit;

        // 전체 리스트의 크기 체크
        List<SaveMsn> limitedSaveMsns;
        if (startIndex < saveMsns.size()) {
            int endIndex = Math.min(startIndex + limit, saveMsns.size());
            limitedSaveMsns = saveMsns.subList(startIndex, endIndex);
        } else {
            limitedSaveMsns = new ArrayList<>(); // 페이지 번호가 범위를 벗어난 경우 빈 리스트
        }

        int totalPages = (int) Math.ceil((double) saveMsns.size() / limit);
        int startPage = ((pageNumber - 1) / limit) * limit + 1; // 현재 페이지 그룹의 시작 페이지
        int endPage = Math.min(startPage + limit - 1, totalPages); // 현재 페이지 그룹의 끝 페이지

        model.addAttribute("affiliate", mediaCompany);
        model.addAttribute("aidx", aidx);
        model.addAttribute("saveMsns",limitedSaveMsns);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "affiliateSightseeingCurrent";
    }

    //////////////////////////////////////
    //////////////////////////////////////
    //////////////////////////////////////

    
    
    @GetMapping({"/{aidx}/affiliateSearchDaily","/{aidx}/affiliateSearchDaily/","/{aidx}/affiliateSearchDaily/{pageNumber}"})
    public String affiliateSearchDaily(HttpSession session, @PathVariable(required = true, value = "aidx") int aidx, @PathVariable(required = false, value = "pageNumber") Integer pageNumber, Model model) {
        Member sessionMember = (Member) session.getAttribute("member");

        MediaCompany mediaCompany = mediaCompanyService.getMediaCompany(aidx);
        if (mediaCompany == null) {
            return "error/404";
        }

        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료
        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }

        MediaCompany target = mediaCompanyService.getMediaCompany(aidx);
        List<SearchMsnDailyStat> searchMsnDailyStats = searchMsnDailyService.findByMediaCompany(aidx);
        Collections.reverse(searchMsnDailyStats);
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        int limit = 20;
        int startIndex = (pageNumber - 1) * limit;

        List<SearchMsnDailyStat> limitedSearchMsnDailyStats;
        if (startIndex < searchMsnDailyStats.size()) {
            int endIndex = Math.min(startIndex + limit, searchMsnDailyStats.size());
            limitedSearchMsnDailyStats = searchMsnDailyStats.subList(startIndex, endIndex);
        } else {
            limitedSearchMsnDailyStats = new ArrayList<>(); // 페이지 번호가 범위를 벗어난 경우 빈 리스트
        }
        // 전체 페이지 수 계산
        int totalPages = (int) Math.ceil((double) searchMsnDailyStats.size() / limit);
        int startPage = ((pageNumber - 1) / limit) * limit + 1; // 현재 페이지 그룹의 시작 페이지
        int endPage = Math.min(startPage + limit - 1, totalPages); // 현재 페이지 그룹의 끝 페이지

        model.addAttribute("affiliate", mediaCompany);
        model.addAttribute("searchMsnDailyStats", limitedSearchMsnDailyStats);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "affiliateSearchDaily";
    }


    @Operation(summary = "매체사-검색미션데일리 통계 검색", description = "매체사-검색미션 데일리 통계를 검색합니다")
    @PostMapping({"/{aidx}/affiliateSearchDaily/search","/{aidx}/affiliateSearchDaily/search/","/{aidx}/affiliateSearchDaily/search/{pageNumber}"})
    @ResponseBody
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
            @ApiResponse(responseCode = "500", description = "검색 중 예기치않은 오류발생")
    })
    public Map<String, Object> searchAffiliateSearchDaily(HttpSession session, @PathVariable(required = true, value = "aidx") int aidx, @PathVariable(required = false, value = "pageNumber") Integer pageNumber, Model model, @RequestBody MediaCompanySearchDailySearchDto dto) {
        Member sessionMember = (Member) session.getAttribute("member");

        MediaCompany mediaCompany = mediaCompanyService.getMediaCompany(aidx);
        Map<String, Object> response = new HashMap<>();
        if (mediaCompany == null) {
            response.put("error", "404"); // 멤버가 없는 경우
            return response;
        }

        if (sessionMember == null) {
            response.put("error", "403"); // 멤버가 없는 경우
            return response;
        }

        List<SearchMsnDailyStat> target = searchMsnDailyService.findByMediaCompany(aidx);

        List<SearchMsnDailyStat> searchMsnDailyStats = searchMsnDailyService.searchSearchMsnDailyByAffiliate(target,dto);
        Collections.reverse(searchMsnDailyStats);
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        int limit = 20;
        int startIndex = (pageNumber - 1) * limit;

        List<SearchMsnDailyStat> limitedSearchMsnDailyStats;
        if (startIndex < searchMsnDailyStats.size()) {
            int endIndex = Math.min(startIndex + limit, searchMsnDailyStats.size());
            limitedSearchMsnDailyStats = searchMsnDailyStats.subList(startIndex, endIndex);
        } else {
            limitedSearchMsnDailyStats = new ArrayList<>(); // 페이지 번호가 범위를 벗어난 경우 빈 리스트
        }
        // 전체 페이지 수 계산
        int totalPages = (int) Math.ceil((double) searchMsnDailyStats.size() / limit);
        int startPage = ((pageNumber - 1) / limit) * limit + 1; // 현재 페이지 그룹의 시작 페이지
        int endPage = Math.min(startPage + limit - 1, totalPages); // 현재 페이지 그룹의 끝 페이지


        response.put("affiliate", mediaCompany);
        response.put("searchMsnDailyStats", limitedSearchMsnDailyStats);
        response.put("currentPage", pageNumber);
        response.put("aidx", aidx);
        response.put("startPage", startPage);
        response.put("totalPages", totalPages);
        response.put("endPage", endPage);

        return response;
    }

    @Operation(summary = "에러 방지", description = "검색 재진입시 원래 페이지로 리턴 ")
    @GetMapping("/{aidx}/affiliateSearchDaily/search/{pageNumber}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
    })
    public String  searchAffiliateSearchDaily_return(@PathVariable(required = true, value = "aidx") int aidx,@PathVariable(required = false,value = "pageNumber") Integer pageNumber,HttpSession session ,Model model){
        Member sessionMember = (Member) session.getAttribute("member");

        MediaCompany mediaCompany = mediaCompanyService.getMediaCompany(aidx);
        if (mediaCompany == null) {
            return "error/404";
        }

        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료
        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }

        MediaCompany target = mediaCompanyService.getMediaCompany(aidx);
        List<SearchMsnDailyStat> searchMsnDailyStats = searchMsnDailyService.findByMediaCompany(aidx);
        Collections.reverse(searchMsnDailyStats);
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        int limit = 20;
        int startIndex = (pageNumber - 1) * limit;

        List<SearchMsnDailyStat> limitedSearchMsnDailyStats;
        if (startIndex < searchMsnDailyStats.size()) {
            int endIndex = Math.min(startIndex + limit, searchMsnDailyStats.size());
            limitedSearchMsnDailyStats = searchMsnDailyStats.subList(startIndex, endIndex);
        } else {
            limitedSearchMsnDailyStats = new ArrayList<>(); // 페이지 번호가 범위를 벗어난 경우 빈 리스트
        }
        // 전체 페이지 수 계산
        int totalPages = (int) Math.ceil((double) searchMsnDailyStats.size() / limit);
        int startPage = ((pageNumber - 1) / limit) * limit + 1; // 현재 페이지 그룹의 시작 페이지
        int endPage = Math.min(startPage + limit - 1, totalPages); // 현재 페이지 그룹의 끝 페이지

        model.addAttribute("affiliate", mediaCompany);
        model.addAttribute("searchMsnDailyStats", limitedSearchMsnDailyStats);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "affiliateSearchDaily";
    }


    @Operation(summary = "매체사-미션 리포트 엑셀 다운로드", description = "매체사-정답미션 데일리 통계 엑셀파일을 다운로드합니다")
    @GetMapping("/{aidx}/affiliateSearchDaily/excel/download")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "500", description = "예기치않은 오류발생")
    })
    public ResponseEntity<Void> searchDailyExcelDownload(@PathVariable(required = true, value = "aidx") int aidx,HttpServletResponse response)throws IOException {

        try (Workbook wb = new XSSFWorkbook()) {
            List<SearchMsnDailyStat> searchMsnDailyStats = searchMsnDailyService.findByMediaCompany(aidx);
            String companyName = mediaCompanyService.getMediaCompany(aidx).getCompanyName();
            Collections.reverse(searchMsnDailyStats);
            Sheet sheet = searchMsnDailyService.excelDownload(searchMsnDailyStats,wb);
            if(sheet !=null) {
                String name = "검색미션 데일리 리포트"+companyName+".xlsx";
                String fileName = URLEncoder.encode(name, StandardCharsets.UTF_8);
                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
                wb.write(response.getOutputStream());
                response.flushBuffer();
                return ResponseEntity.status(HttpStatus.OK).build();
            }
            else
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }




    @GetMapping({"/{aidx}/affiliateSearchSum","/{aidx}/affiliateSearchSum/","/{aidx}/affiliateSearchSum/{pageNumber}"})
    public String affiliateSearchSum(HttpSession session, @PathVariable(required = true, value = "aidx") int aidx, @PathVariable(required = false, value = "pageNumber") Integer pageNumber, Model model) {
        Member sessionMember = (Member) session.getAttribute("member");
        MediaCompany mediaCompany = mediaCompanyService.getMediaCompany(aidx);

        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료

        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }

        List<SearchMsnSumStat> searchMsnSumStats = searchMsnSumStatService.getSearchMsnSumStatsByAffiliate(aidx);
        Collections.reverse(searchMsnSumStats);
        if (searchMsnSumStats.size() > 30) {
            searchMsnSumStats = searchMsnSumStats.subList(0, 30);
        }


        int totalLandingCount = searchMsnSumStats.stream().mapToInt(SearchMsnSumStat::getLandingCnt).sum();  // 랜딩카운트 합
        int totalPartCount =  searchMsnSumStats.stream().mapToInt(SearchMsnSumStat::getPartCnt).sum();  // 참여카운트 합

        model.addAttribute("affiliate", mediaCompany);
        model.addAttribute("searchMsnSumStats", searchMsnSumStats);
        model.addAttribute("totalLandingCount",totalLandingCount);
        model.addAttribute("totalPartCount",totalPartCount);
        return "affiliateSearchSum";
    }


    @Operation(summary = "매체사-정답미션데일리 통계 검색", description = "매체사-정답미션 데일리 통계를 검색합니다")
    @PostMapping({"/{aidx}/affiliateSearchSum/search","/{aidx}/affiliateSearchSum/search/","/{aidx}/affiliateSearchSum/search/{pageNumber}"})
    @ResponseBody
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
            @ApiResponse(responseCode = "500", description = "검색 중 예기치않은 오류발생")
    })
    public Map<String, Object> searchAffiliateSearchSum(HttpSession session, @PathVariable(required = true, value = "aidx") int aidx, @PathVariable(required = false, value = "pageNumber") Integer pageNumber, Model model, @RequestBody MediaCompanySearchSumSearchDto dto) {
        Member sessionMember = (Member) session.getAttribute("member");

        MediaCompany mediaCompany = mediaCompanyService.getMediaCompany(aidx);
        Map<String, Object> response = new HashMap<>();
        if (mediaCompany == null) {
            response.put("error", "404"); // 멤버가 없는 경우
            return response;
        }

        if (sessionMember == null) {
            response.put("error", "403"); // 멤버가 없는 경우
            return response;
        }

        List<SearchMsnSumStat> target = searchMsnSumStatService.getSearchMsnSumStatsByAffiliate(aidx);

        List<SearchMsnSumStat> searchMsnSumStats = searchMsnSumStatService.searchByAffiliate(target,dto);
        Collections.reverse(searchMsnSumStats);
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        int limit = 30;
        int startIndex = (pageNumber - 1) * limit;

        List<SearchMsnSumStat> limitedSearchMsnSumStats;
        if (startIndex < searchMsnSumStats.size()) {
            int endIndex = Math.min(startIndex + limit, searchMsnSumStats.size());
            limitedSearchMsnSumStats = searchMsnSumStats.subList(startIndex, endIndex);
        } else {
            limitedSearchMsnSumStats = new ArrayList<>(); // 페이지 번호가 범위를 벗어난 경우 빈 리스트
        }
        // 전체 페이지 수 계산
        int totalPages = (int) Math.ceil((double) searchMsnSumStats.size() / limit);
        int startPage = ((pageNumber - 1) / limit) * limit + 1; // 현재 페이지 그룹의 시작 페이지
        int endPage = Math.min(startPage + limit - 1, totalPages); // 현재 페이지 그룹의 끝 페이지

        int totalLandingCount = searchMsnSumStats.stream().mapToInt(SearchMsnSumStat::getLandingCnt).sum();  // 랜딩카운트 합
        int totalPartCount =  searchMsnSumStats.stream().mapToInt(SearchMsnSumStat::getPartCnt).sum();  // 참여카운트 합


        response.put("affiliate", mediaCompany);
        response.put("searchMsnSumStats", limitedSearchMsnSumStats);
        response.put("currentPage", pageNumber);
        response.put("aidx", aidx);
        response.put("startPage", startPage);
        response.put("endPage", endPage);
        response.put("totalPages", totalPages);
        response.put("totalLandingCount",totalLandingCount);
        response.put("totalPartCount",totalPartCount);

        return response;
    }

    @Operation(summary = "에러 방지", description = "검색 재진입시 원래 페이지로 리턴 ")
    @GetMapping("/{aidx}/affiliateSearchSum/search/{pageNumber}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
    })
    public String searchAffiliateSearchSum_return(@PathVariable(required = true, value = "aidx") int aidx,@PathVariable(required = false,value = "pageNumber") Integer pageNumber,HttpSession session ,Model model){
        Member sessionMember = (Member) session.getAttribute("member");
        MediaCompany mediaCompany = mediaCompanyService.getMediaCompany(aidx);

        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료

        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }

        List<SearchMsnSumStat> searchMsnSumStats = searchMsnSumStatService.getSearchMsnSumStatsByAffiliate(aidx);
        Collections.reverse(searchMsnSumStats);
        if (searchMsnSumStats.size() > 30) {
            searchMsnSumStats = searchMsnSumStats.subList(0, 30);
        }


        int totalLandingCount = searchMsnSumStats.stream().mapToInt(SearchMsnSumStat::getLandingCnt).sum();  // 랜딩카운트 합
        int totalPartCount =  searchMsnSumStats.stream().mapToInt(SearchMsnSumStat::getPartCnt).sum();  // 참여카운트 합

        model.addAttribute("affiliate", mediaCompany);
        model.addAttribute("searchMsnSumStats", searchMsnSumStats);
        model.addAttribute("totalLandingCount",totalLandingCount);
        model.addAttribute("totalPartCount",totalPartCount);
        return "affiliateSearchSum";
    }


    @Operation(summary = "매체사-미션 리포트 엑셀 다운로드", description = "매체사-정답미션 데일리 통계 엑셀파일을 다운로드합니다")
    @GetMapping("/{aidx}/affiliateSearchSum/excel/download")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "500", description = "예기치않은 오류발생")
    })
    public ResponseEntity<Void> searchSumExcelDownload(@PathVariable(required = true, value = "aidx") int aidx,HttpServletResponse response)throws IOException {

        try (Workbook wb = new XSSFWorkbook()) {
            LocalDate currentDate = LocalDate.now();
            LocalDate past = currentDate.minusMonths(1);
            List<SearchMsnSumStat> list = searchMsnSumStatService.getSearchMsnSumStatsMonthByAffiliate(currentDate,past,aidx);
            Collections.reverse(list);
            int totalLandingCount = list.stream().mapToInt(SearchMsnSumStat::getLandingCnt).sum();  // 랜딩카운트 합
            int totalPartCount =  list.stream().mapToInt(SearchMsnSumStat::getPartCnt).sum();  // 참여카운트
            Sheet sheet = searchMsnSumStatService.excelDownloadCurrent(list,wb,totalLandingCount,totalPartCount);
            if(sheet !=null) {
                String name = "현재 리스트 소진량(저장)" +mediaCompanyService.getMediaCompany(aidx).getCompanyName() +".xlsx";
                String fileName = URLEncoder.encode(name, StandardCharsets.UTF_8);
                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
                wb.write(response.getOutputStream());
                response.flushBuffer();
                return ResponseEntity.status(HttpStatus.OK).build();
            }
            else
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }





    @GetMapping({"/{aidx}/affiliateSearchCurrent","/{aidx}/affiliateSearchCurrent/","/{aidx}/affiliateSearchCurrent/{pageNumber}"})
    public String affiliateSearchCurrentList(HttpSession session, @PathVariable(required = true, value = "aidx") int aidx, @PathVariable(required = false, value = "pageNumber") Integer pageNumber, Model model) {
        Member sessionMember = (Member) session.getAttribute("member");
        MediaCompany mediaCompany = mediaCompanyService.getMediaCompany(aidx);

        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료
        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }

        LocalDateTime now = LocalDateTime.now();
        List<SearchMsn> searchMsns = searchMsnService.findByCurrentListAffiliate(now,aidx);

        // 페이지 번호가 없으면 기본값 1 사용
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }

        // 한 페이지당 최대 15개 데이터
        int limit = 15;
        int startIndex = (pageNumber - 1) * limit;

        // 전체 리스트의 크기 체크
        List<SearchMsn> limitedSearchMsns;
        if (startIndex < searchMsns.size()) {
            int endIndex = Math.min(startIndex + limit, searchMsns.size());
            limitedSearchMsns = searchMsns.subList(startIndex, endIndex);
        } else {
            limitedSearchMsns = new ArrayList<>(); // 페이지 번호가 범위를 벗어난 경우 빈 리스트
        }

        int totalPages = (int) Math.ceil((double) searchMsns.size() / limit);
        int startPage = ((pageNumber - 1) / limit) * limit + 1; // 현재 페이지 그룹의 시작 페이지
        int endPage = Math.min(startPage + limit - 1, totalPages); // 현재 페이지 그룹의 끝 페이지

        model.addAttribute("affiliate", mediaCompany);
        model.addAttribute("aidx", aidx);
        model.addAttribute("searchMsns",limitedSearchMsns);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "affiliateSearchCurrent";
    }


    @Operation(summary = "매체사-정답미션 현재 리스트 검색", description = "매체사-정답미션 현재 리스트를 검색합니다")
    @PostMapping({"/{aidx}/affiliateSearchCurrent/search","/{aidx}/affiliateSearchCurrent/search/","/{aidx}/affiliateSearchCurrent/search/{pageNumber}"})
    @ResponseBody
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
            @ApiResponse(responseCode = "500", description = "검색 중 예기치않은 오류발생")
    })
    public Map<String, Object> searchAffiliateSearchCurrent(HttpSession session, @PathVariable(required = true, value = "aidx") int aidx, @PathVariable(required = false, value = "pageNumber") Integer pageNumber, Model model, @RequestBody MediaCompanySearchCurrentSearchDto dto) {
        Member sessionMember = (Member) session.getAttribute("member");

        MediaCompany mediaCompany = mediaCompanyService.getMediaCompany(aidx);
        Map<String, Object> response = new HashMap<>();
        if (mediaCompany == null) {
            response.put("error", "404"); // 멤버가 없는 경우
            return response;
        }

        if (sessionMember == null) {
            response.put("error", "403"); // 멤버가 없는 경우
            return response;
        }

        List<SearchMsn> target = searchMsnService.findByCurrentListAffiliate(LocalDateTime.now(),aidx);

        List<SearchMsn> searchMsnSumStats = searchMsnService.searchSearchMsnCurrentByAffiliate(target,dto);
        Collections.reverse(searchMsnSumStats);
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        int limit = 30;
        int startIndex = (pageNumber - 1) * limit;

        List<SearchMsn> limitedSearchMsn;
        if (startIndex < searchMsnSumStats.size()) {
            int endIndex = Math.min(startIndex + limit, searchMsnSumStats.size());
            limitedSearchMsn = searchMsnSumStats.subList(startIndex, endIndex);
        } else {
            limitedSearchMsn = new ArrayList<>(); // 페이지 번호가 범위를 벗어난 경우 빈 리스트
        }
        // 전체 페이지 수 계산
        int totalPages = (int) Math.ceil((double) searchMsnSumStats.size() / limit);
        int startPage = ((pageNumber - 1) / limit) * limit + 1; // 현재 페이지 그룹의 시작 페이지
        int endPage = Math.min(startPage + limit - 1, totalPages); // 현재 페이지 그룹의 끝 페이지

        response.put("affiliate", mediaCompany);
        response.put("totalPages", totalPages);
        response.put("searchMsns", limitedSearchMsn);
        response.put("currentPage", pageNumber);
        response.put("aidx", aidx);
        response.put("startPage", startPage);
        response.put("endPage", endPage);
        response.put("totalPages", totalPages);

        return response;
    }

    @Operation(summary = "에러 방지", description = "검색 재진입시 원래 페이지로 리턴 ")
    @GetMapping("/{aidx}/affiliateSearchCurrent/search/{pageNumber}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
    })
    public String searchAffiliateSearchCurrent_return(@PathVariable(required = true, value = "aidx") int aidx,@PathVariable(required = false,value = "pageNumber") Integer pageNumber,HttpSession session ,Model model){
        Member sessionMember = (Member) session.getAttribute("member");
        MediaCompany mediaCompany = mediaCompanyService.getMediaCompany(aidx);

        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료
        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }

        LocalDateTime now = LocalDateTime.now();
        List<SearchMsn> searchMsns = searchMsnService.findByCurrentListAffiliate(now,aidx);

        // 페이지 번호가 없으면 기본값 1 사용
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }

        // 한 페이지당 최대 15개 데이터
        int limit = 15;
        int startIndex = (pageNumber - 1) * limit;

        // 전체 리스트의 크기 체크
        List<SearchMsn> limitedSearchMsns;
        if (startIndex < searchMsns.size()) {
            int endIndex = Math.min(startIndex + limit, searchMsns.size());
            limitedSearchMsns = searchMsns.subList(startIndex, endIndex);
        } else {
            limitedSearchMsns = new ArrayList<>(); // 페이지 번호가 범위를 벗어난 경우 빈 리스트
        }

        int totalPages = (int) Math.ceil((double) searchMsns.size() / limit);
        int startPage = ((pageNumber - 1) / limit) * limit + 1; // 현재 페이지 그룹의 시작 페이지
        int endPage = Math.min(startPage + limit - 1, totalPages); // 현재 페이지 그룹의 끝 페이지

        model.addAttribute("affiliate", mediaCompany);
        model.addAttribute("aidx", aidx);
        model.addAttribute("searchMsns",limitedSearchMsns);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "affiliateSearchCurrent";
    }
}
