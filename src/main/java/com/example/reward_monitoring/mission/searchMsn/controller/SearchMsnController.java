package com.example.reward_monitoring.mission.searchMsn.controller;



import com.example.reward_monitoring.general.advertiser.entity.Advertiser;
import com.example.reward_monitoring.general.advertiser.service.AdvertiserService;
import com.example.reward_monitoring.general.member.entity.Member;
import com.example.reward_monitoring.general.member.model.Auth;
import com.example.reward_monitoring.general.member.repository.MemberRepository;
import com.example.reward_monitoring.general.userServer.service.ServerService;
import com.example.reward_monitoring.mission.answerMsn.entity.AnswerMsn;
import com.example.reward_monitoring.mission.searchMsn.dto.*;
import com.example.reward_monitoring.mission.searchMsn.entity.SearchMsn;
import com.example.reward_monitoring.mission.searchMsn.repository.SearchMsnRepository;

import com.example.reward_monitoring.mission.searchMsn.service.SearchMsnService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@Tag(name = "SearchMsn", description = "검색미션 API")
public class SearchMsnController{
    @Autowired
    private SearchMsnRepository searchMsnRepository;
    @Autowired
    private SearchMsnService searchMsnService;
    @Autowired
    private MemberRepository memberRepository;


    @Autowired
    private AdvertiserService advertiserService;

    @Autowired
    private ServerService serverService;


    @Operation(summary = "저장미션 정보 수정", description = "저장미션 정보를 수정합니다")
    @Parameter(name = "idx", description = "수정할 저장미션의 IDX")
    @PostMapping("/Mission/searchWrite/edit/{idx}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 수정됨"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "일치하는 미션을 찾을 수 없음")
    })
    public ResponseEntity<SearchMsn> edit(HttpSession session,
                                          @PathVariable int idx,
                                          @RequestPart(value ="file",required = false)MultipartFile multipartFile,
                                          @RequestPart(value="dto",required = true) SearchMsnEditDto dto,
                                          HttpServletResponse response) {
        Member sessionMember= (Member) session.getAttribute("member");
        if(sessionMember == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } // 세션만료
        Member member =memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }//데이터 없음

        if(member.isNauthSearchMsn()) // 비권한 활성화시
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        if(member.getAuthSearchMsn()== Auth.READ) // 읽기 권한만 존재할경우
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        if(multipartFile != null && !multipartFile.isEmpty()){
            try {
                byte[] fileContent = multipartFile.getBytes();
                String originalFilename = multipartFile.getOriginalFilename();
                dto.setImageData(fileContent);
                dto.setImageName(originalFilename);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }

        SearchMsn edited = searchMsnService.edit(idx, dto);

        if (edited == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        searchMsnRepository.save(edited);
        return ResponseEntity.status(HttpStatus.OK).body(edited);
    }

    @Operation(summary = "검색미션 생성", description = "검색미션 정보를 생성합니다")
    @PostMapping("/Mission/searchWrite/add")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 수정됨"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류로 미션생성이 되지않음")
    })
    public ResponseEntity<Void> add(HttpSession session,
                                    @RequestPart(value ="file",required = false)MultipartFile multipartFile,
                                    @RequestPart(value ="dto",required = true) SearchMsnReadDto dto) {
        Member sessionMember= (Member) session.getAttribute("member");
        if(sessionMember == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } // 세션만료
        Member member =memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }//데이터 없음

        if(member.isNauthSearchMsn()) // 비권한 활성화시
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        if(member.getAuthSearchMsn()== Auth.READ) // 읽기 권한만 존재할경우
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        if(multipartFile != null && !multipartFile.isEmpty()){
            try {
                byte[] fileContent = multipartFile.getBytes();
                String originalFilename = multipartFile.getOriginalFilename();
                dto.setImageData(fileContent);
                dto.setImageName(originalFilename);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }

        SearchMsn created = searchMsnService.add(dto);
        if (created == null)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        searchMsnRepository.save(created);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "검색미션 정보 요청", description = "IDX와 일치하는 미션정보를 반환합니다")
    @Parameter(name = "idx", description = "관리자 IDX")
    @GetMapping("/Mission/searchList/get/{idx}")  //미션 검색 (idx)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "미션 검색 완료 "),
            @ApiResponse(responseCode = "204", description = "일치하는 미션을 찾을 수 없음"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
    })
    public ResponseEntity<SearchMsn> getSearchMsn(HttpSession session, @PathVariable int idx) {
        Member sessionMember= (Member) session.getAttribute("member");
        if(sessionMember == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } // 세션만료
        Member member =memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }//데이터 없음

        if(member.isNauthSearchMsn()) // 비권한 활성화시
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        SearchMsn target = searchMsnService.getSearchMsn(idx);
        return (target != null) ?
                ResponseEntity.status(HttpStatus.OK).body(target) :
                ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "전체 미션정보 요청", description = "전체 미션 정보를 반환합니다")
    @GetMapping("/Mission/searchList/searchMsns")  //전체 광고주 리스트 반환
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음")
    })
    public ResponseEntity<List<SearchMsn>> getSearchMsns(HttpSession session){
        Member sessionMember= (Member) session.getAttribute("member");
        if(sessionMember == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } // 세션만료
        Member member =memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }//데이터 없음
        if(member.isNauthSearchMsn()) // 비권한 활성화시
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();


        return ResponseEntity.status(HttpStatus.OK).body(searchMsnService.getSearchMsns());
    }

    @Operation(summary = "미션 삭제", description = "IDX와 일치하는 단일 미션정보를 삭제합니다")
    @Parameter(name = "idx", description = "미션 IDX")
    @DeleteMapping("/Mission/searchList/delete/{idx}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 완료 "),
            @ApiResponse(responseCode = "400", description = "일치하는 미션이 없음"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음")
    })
    public ResponseEntity<String> delete(HttpSession session,@PathVariable int idx) throws IOException {

        Member sessionMember= (Member) session.getAttribute("member");
        if(sessionMember == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } // 세션만료
        Member member =memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }//데이터 없음

        if(member.isNauthSearchMsn()) // 비권한 활성화시
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        if(member.getAuthSearchMsn()== Auth.READ) // 읽기 권한만 존재할경우
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();


        SearchMsn deleted = searchMsnService.delete(idx);
        return (deleted != null) ?
                ResponseEntity.status(HttpStatus.NO_CONTENT).build() :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

    }

    @Operation(summary = "미션 삭제", description = "미션을 삭제(숨김처리합니다)")
    @Parameter(name = "idx", description = "미션 IDX")
    @GetMapping("/Mission/searchList/hidden/{idx}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 완료 "),
            @ApiResponse(responseCode = "400", description = "일치하는 미션이 없음"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음")
    })
    public ResponseEntity<Void>  hidden(HttpSession session, @PathVariable int idx)throws IOException {

        Member sessionMember= (Member) session.getAttribute("member");
        if(sessionMember == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } // 세션만료

        Member member =memberRepository.findById( sessionMember.getId());
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }//데이터 없음

        if(member.isNauthAnswerMsn()) // 비권한 활성화시
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        if(member.getAuthAnswerMsn()== Auth.READ) // 읽기 권한만 존재할경우
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();


        boolean result = searchMsnService.hidden(idx);
        return (result) ?
                ResponseEntity.status(HttpStatus.NO_CONTENT).build():
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }


    @Operation(summary = "검색미션 검색", description = "조건에 맞는 검색미션을 검색합니다")
    @PostMapping("/Mission/searchList/search")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "검색 중 예기치않은 오류발생")
    })
    public ResponseEntity<List<SearchMsn>> searchSearchMsn(HttpSession session,@RequestBody SearchMsnSearchDto dto){

        Member sessionMember= (Member) session.getAttribute("member");
        if(sessionMember == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } // 세션만료
        Member member =memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }//데이터 없음
        if(member.isNauthSearchMsn()) // 비권한 활성화시
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        List<SearchMsn> result = searchMsnService.searchSearchMsn(dto);
        return (result != null) ?
                ResponseEntity.status(HttpStatus.OK).body(result): // 일치하는 결과가 없을경우 빈 리스트 반환
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    @Operation(summary = "검색미션 현재 리스트 소진량 검색", description = "현재 리스트 소진량 페이지에서 조건에 맞는 검색미션을 검색합니다")
    @PostMapping("/Mission/searchList/searchMsnByConsumed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "검색 중 예기치않은 오류발생")
    })
    public ResponseEntity<List<SearchMsn>> searchSearchMsnByConsumed(HttpSession session, @RequestBody SearchMsnSearchDto dto){
        Member sessionMember= (Member) session.getAttribute("member");
        if(sessionMember == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } // 세션만료

        Member member =memberRepository.findById( sessionMember.getId());
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }//데이터 없음

        if(member.isNauthCurSearch()) // 비권한 활성화시
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();


        List<SearchMsn> result = searchMsnService.searchSearchMsnByConsumed(dto);
        return (result != null) ?
                ResponseEntity.status(HttpStatus.OK).body(result): // 일치하는 결과가 없을경우 빈 리스트 반환
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @Operation(summary = "모든 미션 종료", description = "!!DB의 모든 미션을 종료하고 비노출처리합니다!!")
    @GetMapping("/Mission/searchList/endAll")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모든 미션 종료 처리"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "작업 중 예기치않은 오류발생")
    })
    public ResponseEntity<Void> allMissionEnd(HttpSession session){
        Member sessionMember= (Member) session.getAttribute("member");
        if(sessionMember == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } // 세션만료

        Member member =memberRepository.findById( sessionMember.getId());
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }//데이터 없음

        if(member.isNauthSearchMsn()) // 비권한 활성화시
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        if(member.getAuthSearchMsn()== Auth.READ) // 읽기 권한만 존재할경우
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();


        boolean result = searchMsnService.allMissionEnd();

        return (result) ?
                ResponseEntity.status(HttpStatus.OK).build():
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }




    @Operation(summary = "엑셀 다운로드", description = "검색미션 리스트 엑셀파일을 다운로드합니다")
    @GetMapping("/Mission/searchList/excel/download")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "500", description = "예기치않은 오류발생")
    })
    public ResponseEntity<Void> excelDownload(HttpServletResponse response) throws IOException {
        try (Workbook wb = new XSSFWorkbook()) {
            List<SearchMsn> list = searchMsnService.getSearchMsns();
            Sheet sheet = searchMsnService.excelDownload(list, wb);

            if (sheet != null) {
                String fileName = URLEncoder.encode("검색미션 리스트.xlsx", StandardCharsets.UTF_8);
                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
                wb.write(response.getOutputStream());
                response.flushBuffer();
                return ResponseEntity.status(HttpStatus.OK).build();
            } else
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }


    }

    @Operation(summary = "엑셀 업로드", description = "업로드한 엑셀파일을 DTO로 변환하여 DB에 추가합니다.")
    @PostMapping("/Mission/searchList/excel/upload")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "500", description = "엑셀파일의 문제로 인한 데이터 삽입 실패")
    })
    public ResponseEntity<Void> excelUpload(HttpSession session, @RequestParam("file") MultipartFile file)throws IOException {

        Member sessionMember= (Member)session.getAttribute("member");
        if(sessionMember == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } // 세션만료

        Member member =memberRepository.findById( sessionMember.getId());
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }//데이터 없음

        if(member.isNauthAnswerMsn()) // 비권한 활성화시
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        if(member.getAuthAnswerMsn()== Auth.READ) // 읽기 권한만 존재할경우
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        try {
            boolean result = searchMsnService.readExcel(file);
            return (result) ?
                    ResponseEntity.status(HttpStatus.OK).build():
                    ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }catch( IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Operation(summary = "미션 사용여부 변경", description = "미션 사용여부를 변경합니다")
    @PostMapping("/Mission/searchList/reEngagement/{idx}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "")
    })
    public ResponseEntity<Void> changeMissionActive(HttpSession session, @PathVariable int idx , @RequestBody SearchMsnActiveDto dto)throws IOException {


        boolean result = searchMsnService.changeMissionActive(idx,dto);

        return (result) ?
                ResponseEntity.status(HttpStatus.OK).build():
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @Operation(summary = "미션 노출여부 변경", description = "미션 노출여부를 변경합니다.")
    @PostMapping("/Mission/searchList/expose/{idx}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "엑셀파일의 문제로 인한 데이터 삽입 실패")
    })
    public ResponseEntity<Void> changeMissionExpose(HttpSession session, @PathVariable int idx , @RequestBody SearchMsnExposeDto dto)throws IOException {

        boolean result = searchMsnService.changeMissionExpose(idx,dto);

        return (result) ?
                ResponseEntity.status(HttpStatus.OK).build():
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @Operation(summary = "가능일 변경", description = "가능일을 변경합니다")
    @PostMapping("/Mission/searchList/{idx}/changeDay")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "엑셀파일의 문제로 인한 데이터 삽입 실패")
    })
    public ResponseEntity<Void> changeAbleDay(HttpSession session, SearchMsnAbleDayDto dto, @PathVariable int idx)throws IOException {



        boolean result = searchMsnService.changeAbleDay(dto,idx);

        return (result) ?
                ResponseEntity.status(HttpStatus.OK).build():
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }


    @GetMapping({"/Mission/searchList/{pageNumber}","/Mission/searchList","/Mission/searchList/"})
    public String searchList(@PathVariable(required = false,value = "pageNumber") Integer pageNumber,HttpSession session, Model model) {
        Member sessionMember = (Member) session.getAttribute("member");
        List<Advertiser> advertisers = advertiserService.getAdvertisers();
        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료
        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }

        List<SearchMsn> searchMsns = searchMsnService.getSearchMsns();
        Collections.reverse(searchMsns);

        // 페이지 번호가 없으면 기본값 1 사용
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        // 한 페이지당 최대 10개 데이터
        int limit = 10;
        int startIndex = (pageNumber - 1) * limit;


        // 전체 리스트의 크기 체크
        List<SearchMsn> limitedSearchMsns;
        if (startIndex < searchMsns.size()) {
            int endIndex = Math.min(startIndex + limit, searchMsns.size());
            limitedSearchMsns = searchMsns.subList(startIndex, endIndex);
        } else {
            limitedSearchMsns = new ArrayList<>(); // 페이지 번호가 범위를 벗어난 경우 빈 리스트
        }
        // 전체 페이지 수 계산
        int totalPages = (int) Math.ceil((double) searchMsns.size() / limit);
        int startPage = ((pageNumber - 1) / limit) * limit + 1; // 현재 페이지 그룹의 시작 페이지
        int endPage = Math.min(startPage + limit - 1, totalPages); // 현재 페이지 그룹의 끝 페이지


        model.addAttribute("searchMsns",limitedSearchMsns);
        model.addAttribute("advertisers", advertisers);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "searchList";
    }



    @GetMapping("/Mission/searchWrite")
    public String searchWrite(HttpSession session) throws IOException {

        Member sessionMember = (Member) session.getAttribute("member");
        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료
        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }

        return "searchWrite";

    }
    @GetMapping("/Mission/searchList/setMissionIsUsed/{idx}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러 발생")
    })
    public ResponseEntity<Void>setMissionIsUsed(@PathVariable(required = true,value = "idx") int idx,HttpSession session)throws IOException {

        Member sessionMember= (Member) session.getAttribute("member");

        if(sessionMember == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } // 세션만료

        Member member =memberRepository.findById( sessionMember.getId());
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }//데이터 없음

        if(member.isNauthAnswerMsn()) // 비권한 활성화시
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        if(member.getAuthAnswerMsn()== Auth.READ) // 읽기 권한만 존재할경우
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        boolean result = searchMsnService.setMissionIsUsed(idx);
        return (result) ?
                ResponseEntity.status(HttpStatus.OK).build():
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }


    @GetMapping("/Mission/searchList/setMissionIsUsedFalse/{idx}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러 발생")
    })
    public ResponseEntity<Void>setMissionIsUsedFalse(@PathVariable(required = true,value = "idx") int idx,HttpSession session)throws IOException {

        Member sessionMember= (Member) session.getAttribute("member");

        if(sessionMember == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } // 세션만료

        Member member =memberRepository.findById( sessionMember.getId());
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }//데이터 없음

        if(member.isNauthAnswerMsn()) // 비권한 활성화시
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        if(member.getAuthAnswerMsn()== Auth.READ) // 읽기 권한만 존재할경우
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        boolean result = searchMsnService.setMissionIsUsedFalse(idx);
        return (result) ?
                ResponseEntity.status(HttpStatus.OK).build():
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }


    @GetMapping("/Mission/searchList/setMissionIsView/{idx}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러 발생")
    })
    public ResponseEntity<Void>setMissionIsView(@PathVariable(required = true,value = "idx") int idx,HttpSession session)throws IOException {

        Member sessionMember= (Member) session.getAttribute("member");

        if(sessionMember == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } // 세션만료

        Member member =memberRepository.findById( sessionMember.getId());
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }//데이터 없음

        if(member.isNauthAnswerMsn()) // 비권한 활성화시
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        if(member.getAuthAnswerMsn()== Auth.READ) // 읽기 권한만 존재할경우
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        boolean result = searchMsnService.setMissionIsView(idx);
        return (result) ?
                ResponseEntity.status(HttpStatus.OK).build():
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }


    @GetMapping("/Mission/searchList/setMissionIsViewFalse/{idx}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러 발생")
    })

    public ResponseEntity<Void>setMissionIsViewFalse(@PathVariable(required = true,value = "idx") int idx,HttpSession session)throws IOException {

        Member sessionMember= (Member) session.getAttribute("member");
        if(sessionMember == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } // 세션만료

        Member member =memberRepository.findById( sessionMember.getId());
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }//데이터 없음

        if(member.isNauthAnswerMsn()) // 비권한 활성화시
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        if(member.getAuthAnswerMsn()== Auth.READ) // 읽기 권한만 존재할경우
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        boolean result = searchMsnService.setMissionIsViewFalse(idx);
        return (result) ?
                ResponseEntity.status(HttpStatus.OK).build():
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/Mission/searchWrite/{idx}")
    public String searchEdit(HttpSession session, Model model , @PathVariable int idx) {

        Member sessionMember = (Member) session.getAttribute("member");
        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료
        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }


        SearchMsn searchMsn = searchMsnService.getSearchMsn(idx);
        if(searchMsn==null)
            return "error/404";
        model.addAttribute("searchMsn", searchMsn);
        return "searchWrite";
    }

    @GetMapping("/Mission/searchCurrentList")
    public String searchCurrentList(HttpSession session, Model model) {
        Member sessionMember = (Member) session.getAttribute("member");
        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료
        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }

        List<SearchMsn> searchMsns = searchMsnService.getSearchMsns();
        model.addAttribute("searchMsns",searchMsns);
        return "searchCurrentList";
    }

    @GetMapping("/Mission/searchMultiTempList")
    public String searchMultiTempList(HttpSession session, Model model) {
        Member sessionMember = (Member) session.getAttribute("member");
        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료
        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }

        return "searchMultiTempList";
    }


}
