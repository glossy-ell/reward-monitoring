package com.example.reward_monitoring.mission.answerMsn.controller;


import com.example.reward_monitoring.general.advertiser.entity.Advertiser;
import com.example.reward_monitoring.general.advertiser.service.AdvertiserService;
import com.example.reward_monitoring.general.member.entity.Member;
import com.example.reward_monitoring.general.member.model.Auth;
import com.example.reward_monitoring.general.member.repository.MemberRepository;
import com.example.reward_monitoring.general.userServer.entity.Server;
import com.example.reward_monitoring.general.userServer.service.ServerService;
import com.example.reward_monitoring.mission.answerMsn.dto.*;
import com.example.reward_monitoring.mission.answerMsn.entity.AnswerMsn;
import com.example.reward_monitoring.mission.answerMsn.repository.AnswerMsnRepository;
import com.example.reward_monitoring.mission.answerMsn.service.AnswerMsnService;
import com.example.reward_monitoring.statistics.answerMsnStat.daily.entity.AnswerMsnDailyStat;
import com.example.reward_monitoring.statistics.answerMsnStat.daily.service.AnswerMsnDailyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Controller
@Tag(name = "AnswerMsn", description = "정답미션관리 API")
public class AnswerMsnController {
    @Autowired
    private AnswerMsnRepository answerMsnRepository;

    @Autowired
    private AnswerMsnService answerMsnService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AdvertiserService advertiserService;

    @Autowired
    private ServerService serverService;


    @Autowired
    private AnswerMsnDailyService answerMsnDailyService;



    @Operation(summary = "정답미션 정보 수정", description = "정답미션 정보를 수정합니다")
    @Parameter(name = "idx", description = "수정할 정답미션의 IDX")
    @PostMapping("/Mission/quizWrite/edit/{idx}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 수정됨"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "일치하는 미션을 찾을 수 없음/이미지 업로드 오류")
    })
    public ResponseEntity<AnswerMsn> edit(HttpSession session,
                                          @PathVariable int idx,
                                          @RequestPart(value ="file",required = false)MultipartFile multipartFile,
                                          @RequestPart(value="dto",required = true) AnswerMsnEditDto dto,
                                          HttpServletResponse response) throws IOException {
        Member sessionMember= (Member) session.getAttribute("member");
        boolean result =true;

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

        

        if(multipartFile != null && !multipartFile.isEmpty()){
            try {
                byte[] fileContent = multipartFile.getBytes();
                String originalFilename = multipartFile.getOriginalFilename();
                log.info(originalFilename);
                dto.setImageData(fileContent);
                dto.setImageName(originalFilename);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }



        AnswerMsn edited = answerMsnService.edit(idx,dto);
        if(edited == null){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        String directoryPath = "C:/images/quiz/";
        String subDirectoryPath = directoryPath + edited.getIdx() + "/";
        File subDirectory = new File(subDirectoryPath);
        if (!subDirectory.exists()) {
            result =subDirectory.mkdirs(); //
        }
        if(!result)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        File destinationFile = new File(subDirectoryPath + edited.getImageName());
        if(multipartFile != null)
            multipartFile.transferTo(destinationFile);
        edited.setImagePath(subDirectoryPath + edited.getImageName());


        answerMsnRepository.save(edited);
        return ResponseEntity.status(HttpStatus.OK).body(edited);
    }


    @Operation(summary = "정답미션 생성", description = "정답미션 정보를 생성합니다")
    @PostMapping("/Mission/quizWrite/add")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 수정됨"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<Void> add(HttpSession session,
            @RequestPart(value ="file",required = false)MultipartFile multipartFile,
            @RequestPart(value ="dto",required = true)AnswerMsnReadDto dto) throws IOException {
        Member sessionMember= (Member) session.getAttribute("member");
        boolean result =true;
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
        AnswerMsn created = answerMsnService.add(dto);
        if(created == null)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        String directoryPath = "C:/images/";
        String subDirectoryPath = directoryPath + created.getIdx() + "/";
        File subDirectory = new File(subDirectoryPath);
        if (!subDirectory.exists()) {
            result =subDirectory.mkdirs(); //
        }
        if(!result)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        File destinationFile = new File(subDirectoryPath + created.getImageName());
        if(multipartFile != null)
            multipartFile.transferTo(destinationFile);
        created.setImagePath(subDirectoryPath + created.getImageName());

        answerMsnRepository.save(created);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }




    @Operation(summary = "정답미션 정보 요청", description = "IDX와 일치하는 미션정보를 반환합니다")
    @Parameter(name = "idx", description = "관리자 IDX")
    @GetMapping("/Mission/quizList/get/{idx}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "미션 검색 완료 "),
            @ApiResponse(responseCode = "204", description = "일치하는 미션을 찾을 수 없음"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
    })
    public ResponseEntity<AnswerMsn> getAnswerMsn(HttpSession session,@PathVariable int idx){
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


        AnswerMsn target = answerMsnService.getAnswerMsn(idx);
        return (target != null) ?
                ResponseEntity.status(HttpStatus.OK).body(target):
                ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }



    @Operation(summary = "전체 미션정보 요청", description = "전체 미션 정보를 반환합니다")
    @GetMapping("/Mission/quizList/answerMsns")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
    })
    public ResponseEntity<List<AnswerMsn>> getAnswerMsns(HttpSession session){
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

        return ResponseEntity.status(HttpStatus.OK).body(answerMsnService.getAnswerMsns());
    }


    @Operation(summary = "미션 삭제", description = "IDX와 일치하는 단일 미션정보를 삭제합니다")
    @Parameter(name = "idx", description = "미션 IDX")
    @DeleteMapping("/Mission/quizList/delete/{idx}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 완료 "),
            @ApiResponse(responseCode = "400", description = "일치하는 미션이 없음"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음")
    })
    public ResponseEntity<String> delete(HttpSession session, @PathVariable int idx)throws IOException {

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


        AnswerMsn deleted  = answerMsnService.delete(idx);
        return (deleted != null) ?
                ResponseEntity.status(HttpStatus.NO_CONTENT).build():
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @Operation(summary = "미션 삭제", description = "미션을 삭제(숨김처리합니다)")
    @Parameter(name = "idx", description = "미션 IDX")
    @GetMapping("/Mission/quizList/hidden/{idx}")
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


        boolean result = answerMsnService.hidden(idx);
        return (result) ?
                ResponseEntity.status(HttpStatus.NO_CONTENT).build():
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @Operation(summary = "정답미션 검색", description = "조건에 맞는 정답미션을 검색합니다")
    @PostMapping({"/Mission/quizList/search","/Mission/quizList/search/"})
    @ResponseBody
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "검색 중 예기치않은 오류발생")
    })
    public Map<String, Object> searchAnswerMsn(@PathVariable(required = false,value = "pageNumber") Integer pageNumber,HttpSession session, @RequestBody AnswerMsnSearchDto dto){
        Member sessionMember= (Member) session.getAttribute("member");
        Map<String, Object> response = new HashMap<>();
        if(sessionMember == null){
            response.put("error", "404"); // 멤버가 없는 경우
            return response;
        } // 세션만료

        Member member =memberRepository.findById( sessionMember.getId());
        if (member == null) {
            response.put("error", "403"); // 비권한 사용자인 경우
            return response;
        }//데이터 없음

        if(member.isNauthAnswerMsn()) { // 비권한 활성화시
            response.put("error", "403");
            return response;
        }


        List<AnswerMsn> result = answerMsnService.searchAnswerMsn(dto);
        Collections.reverse(result);
        // 페이지 번호가 없으면 기본값 1 사용
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }

        // 한 페이지당 최대 10개 데이터
        int limit = 10;
        int startIndex = (pageNumber - 1) * limit;

        // 전체 리스트의 크기 체크
        List<AnswerMsn> limitedAnswerMsns;
        if (startIndex < result.size()) {
            int endIndex = Math.min(startIndex + limit, result.size());
            limitedAnswerMsns = result.subList(startIndex, endIndex);
        } else {
            limitedAnswerMsns = new ArrayList<>(); // 페이지 번호가 범위를 벗어난 경우 빈 리스트
        }

        int totalPages = (int) Math.ceil((double) result.size() / limit);
        int startPage = ((pageNumber - 1) / limit) * limit + 1; // 현재 페이지 그룹의 시작 페이지
        int endPage = Math.min(startPage + limit - 1, totalPages); // 현재 페이지 그룹의 끝 페이지

        response.put("fullAnswerMsns",result);
        response.put("answerMsns", limitedAnswerMsns);  // limitedMembers 리스트
        response.put("currentPage", pageNumber);  // 현재 페이지 번호
        response.put("totalPages", totalPages);    // 전체 페이지 수
        response.put("startPage",startPage);
        response.put("endPage",endPage);
        return response; // JSON 형태로 반환
    }

    @Operation(summary = "정답미션 페이지검색", description = "이미 검색한 미션의 페이지 이동시 처리하는 컨트롤러입니다")
    @PostMapping("/Mission/quizList/search/{pageNumber}")
    @ResponseBody
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "검색 중 예기치않은 오류발생")
    })

    public Map<String, Object> searchAnswerMsn_page(@PathVariable(required = true,value = "pageNumber") Integer pageNumber,HttpSession session, @RequestBody ResponseDto responseDto){
        Member sessionMember= (Member) session.getAttribute("member");
        Map<String, Object> response = new HashMap<>();
        if(sessionMember == null){
            response.put("error", "404"); // 멤버가 없는 경우
            return response;
        } // 세션만료

        Member member =memberRepository.findById(sessionMember.getId());
        if (member == null) {
            response.put("error", "403"); // 비권한 사용자인 경우
            return response;
        }//데이터 없음

        if(member.isNauthAnswerMsn()) { // 비권한 활성화시
            response.put("error", "403");
            return response;
        }

        List<AnswerMsn> result = responseDto.getInnerAnswerMsns();
        // 페이지 번호가 없으면 기본값 1 사용
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }

        // 한 페이지당 최대 15개 데이터
        int limit = 15;
        int startIndex = (pageNumber - 1) * limit;

        // 전체 리스트의 크기 체크
        List<AnswerMsn> limitedAnswerMsns;
        if (startIndex < result.size()) {
            int endIndex = Math.min(startIndex + limit, result.size());
            limitedAnswerMsns = result.subList(startIndex, endIndex);
        } else {
            limitedAnswerMsns = new ArrayList<>(); // 페이지 번호가 범위를 벗어난 경우 빈 리스트
        }

        int totalPages = (int) Math.ceil((double) result.size() / limit);
        int startPage = ((pageNumber - 1) / limit) * limit + 1; // 현재 페이지 그룹의 시작 페이지
        int endPage = Math.min(startPage + limit - 1, totalPages); // 현재 페이지 그룹의 끝 페이지

        response.put("fullAnswerMsns",result);
        response.put("answerMsns", limitedAnswerMsns);  // limitedMembers 리스트
        response.put("currentPage", pageNumber);  // 현재 페이지 번호
        response.put("totalPages", totalPages);    // 전체 페이지 수
        response.put("startPage",startPage);
        response.put("endPage",endPage);
        return response; // JSON 형태로 반환
    }




    @Operation(summary = "엑셀 다운로드", description = "정답미션 리스트 엑셀파일을 다운로드합니다")
    @GetMapping("/Mission/quizList/excel/download")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "예기치않은 오류발생")
    })
    public ResponseEntity<Void> excelDownload(HttpServletResponse response)throws IOException {
        try (Workbook wb = new XSSFWorkbook()) {
            List<AnswerMsn> list = answerMsnService.getAnswerMsns();
            Sheet sheet = answerMsnService.excelDownload(list,wb);

            if(sheet !=null) {
                String fileName = URLEncoder.encode("정답미션 리스트.xlsx", StandardCharsets.UTF_8);
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

    @Operation(summary = "엑셀 업로드", description = "업로드한 엑셀파일을 DTO로 변환하여 DB에 추가합니다.")
    @PostMapping("/Mission/quizList/excel/upload")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "엑셀파일의 문제로 인한 데이터 삽입 실패")
    })
    public ResponseEntity<Void> excelUpload(HttpSession session,@RequestParam("file") MultipartFile file)throws IOException {

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
        try {
            boolean result = answerMsnService.readExcel(file);
            return (result) ?
                    ResponseEntity.status(HttpStatus.OK).build():
                    ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }catch( IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }



    @Operation(summary = "미션 사용여부 변경", description = "미션 사용여부를 변경합니다")
    @PostMapping("/Mission/quizList/reEngagement/{idx}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "")
    })
    public ResponseEntity<Void> changeMissionReEngagementDay(HttpSession session, @PathVariable int idx , @RequestBody AnswerMsnAbleDayDto dto)throws IOException {

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


        boolean result = answerMsnService.changeMissionReEngagementDay(idx,dto);

        return (result) ?
                ResponseEntity.status(HttpStatus.OK).build():
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }



    @Operation(summary = "미션 노출여부 변경", description = "미션 노출여부를 변경합니다.")
    @PostMapping("/Mission/quizList/expose/{idx}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "엑셀파일의 문제로 인한 데이터 삽입 실패")
    })
    public ResponseEntity<Void> changeMissionExpose(HttpSession session, @PathVariable int idx , @RequestBody AnswerMsnExposeDto dto)throws IOException {
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

        boolean result = answerMsnService.changeMissionExpose(idx,dto);

        return (result) ?
                ResponseEntity.status(HttpStatus.OK).build():
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }



    @Operation(summary = "가능일 변경", description = "가능일을 변경합니다")
    @PostMapping("/Mission/quizList/{idx}/changeDay")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "엑셀파일의 문제로 인한 데이터 삽입 실패")
    })
    public ResponseEntity<Void> changeAbleDay(HttpSession session,AnswerMsnAbleDayDto dto,@PathVariable int idx)throws IOException {

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


        boolean result = answerMsnService.changeAbleDay(dto,idx);

        return (result) ?
                ResponseEntity.status(HttpStatus.OK).build():
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }



    @GetMapping({"/Mission/quizList/{pageNumber}","/Mission/quizList","/Mission/quizList/"})
    public String quizList(@PathVariable(required = false,value = "pageNumber") Integer pageNumber,HttpSession session, Model model) {
        Member sessionMember = (Member) session.getAttribute("member");
        List<Advertiser> advertisers = advertiserService.getAdvertisers();
        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료
        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }

        List<AnswerMsn> answerMsns = answerMsnService.getAnswerMsns();
        Collections.reverse(answerMsns);

        // 페이지 번호가 없으면 기본값 1 사용
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        // 한 페이지당 최대 10개 데이터
        int limit = 10;
        int startIndex = (pageNumber - 1) * limit;


        // 전체 리스트의 크기 체크
        List<AnswerMsn> limitedAnswerMsns;
        if (startIndex < answerMsns.size()) {
            int endIndex = Math.min(startIndex + limit, answerMsns.size());
            limitedAnswerMsns = answerMsns.subList(startIndex, endIndex);
        } else {
            limitedAnswerMsns = new ArrayList<>(); // 페이지 번호가 범위를 벗어난 경우 빈 리스트
        }
        // 전체 페이지 수 계산
        int totalPages = (int) Math.ceil((double) answerMsns.size() / limit);
        int startPage = ((pageNumber - 1) / limit) * limit + 1; // 현재 페이지 그룹의 시작 페이지
        int endPage = Math.min(startPage + limit - 1, totalPages); // 현재 페이지 그룹의 끝 페이지

        model.addAttribute("answerMsns",limitedAnswerMsns);
        model.addAttribute("advertisers", advertisers);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "quizList";
    }



    @GetMapping("/Mission/quizWrite")
    public String quizWrite(HttpSession session,Model model) throws IOException {

        Member sessionMember = (Member) session.getAttribute("member");
        List<Advertiser> advertisers = advertiserService.getAdvertisers();

        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료
        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }

        LocalTime now = LocalTime.now();

        int minute = now.getMinute();
        int roundedMinute = ((minute + 9) / 10) * 10; // 10분 단위로 올림
        if (roundedMinute == 60) {
            now = now.plusHours(1).withMinute(0);
        } else {
            now = now.withMinute(roundedMinute);
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);
        String formattedCurrentTime = now.format(formatter); //현재시간을 시스템에 맞춰 10분단위로 올림


        model.addAttribute("advertisers", advertisers);
        model.addAttribute("currentDateTime", LocalDate.now());
        model.addAttribute("currentTime", formattedCurrentTime);
        model.addAttribute("currentEndDate", LocalDate.now().plusDays(1));
        model.addAttribute("currentEndTime", LocalTime.now().plusHours(1));
        return "quizWrite";

    }

    @GetMapping("/Mission/quizWrite/{idx}")
    public String quizEdit(HttpSession session, Model model , @PathVariable int idx) {

        Member sessionMember = (Member) session.getAttribute("member");
        String image = null;
        List<Advertiser> advertisers = advertiserService.getAdvertisers();
        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료
        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }


        AnswerMsn answerMsn = answerMsnService.getAnswerMsn(idx);

        if(answerMsn==null)
            return "error/404";
        image = answerMsn.getImageName();

        model.addAttribute("answerMsn", answerMsn);
        model.addAttribute("advertisers", advertisers);
        model.addAttribute("currentDateTime", LocalDate.now());
        model.addAttribute("currentTime", LocalTime.now());
        model.addAttribute("currentEndDate", LocalDate.now().plusDays(1));
        model.addAttribute("currentEndTime", LocalTime.now().plusHours(1));
        if(image !=null) {
            try {
                File imageFile = new File(answerMsn.getImagePath());
                byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
                String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                model.addAttribute("image", base64Image ); // URL을 모델에 추가
            } catch (IOException e) {
                return "quizWrite";
            }
        }

        return "quizWrite";
    }





    @GetMapping("/Mission/quizList/setOffMissionIsUsed/{pageNumber}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러 발생")
    })
    public ResponseEntity<Void> setOffMissionIsUsed(@PathVariable(required = false,value = "pageNumber") Integer pageNumber,HttpSession session)throws IOException {

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
        // 페이지 번호가 없으면 기본값 1 사용
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        boolean result = answerMsnService.setOffMissionIsUsed(pageNumber);
        return (result) ?
                ResponseEntity.status(HttpStatus.OK).build():
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @PostMapping("/Mission/quizList/setOffMissionIsUsedSearch/{pageNumber}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러 발생")
    })
    public ResponseEntity<Void> setOffMissionIsUsed_search(@PathVariable(required = true,value = "pageNumber") Integer pageNumber,HttpSession session,@RequestBody ResponseDto responseDto)throws IOException {

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

        List<AnswerMsn> dto = responseDto.getInnerAnswerMsns();
        boolean result = answerMsnService.setOffMissionIsUsed(pageNumber,dto);

        return (result) ?
                ResponseEntity.status(HttpStatus.OK).build():
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }



    @GetMapping("/Mission/quizList/setOffMissionIsView/{pageNumber}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러 발생")
    })
    public ResponseEntity<Void> setOffMissionIsView(@PathVariable(required = false,value = "pageNumber") Integer pageNumber,HttpSession session)throws IOException {

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
        // 페이지 번호가 없으면 기본값 1 사용
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }

        boolean result = answerMsnService.setOffMissionIsView(pageNumber);
        return (result) ?
                ResponseEntity.status(HttpStatus.OK).build():
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @PostMapping("/Mission/quizList/setOffMissionIsViewSearch/{pageNumber}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러 발생")
    })
    public ResponseEntity<Void> setOffMissionIsView_search(@PathVariable(required = true,value = "pageNumber") Integer pageNumber,HttpSession session,@RequestBody ResponseDto responseDto)throws IOException {

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

        List<AnswerMsn> dto = responseDto.getInnerAnswerMsns();
        boolean result = answerMsnService.setOffMissionIsView(pageNumber,dto);

        return (result) ?
                ResponseEntity.status(HttpStatus.OK).build():
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }



    @GetMapping("/Mission/quizList/AllOffMission")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러 발생")
    })
    public ResponseEntity<Void>AllOffMission(HttpSession session)throws IOException {

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

        boolean result = answerMsnService.AllOffMission();
        return (result) ?
                ResponseEntity.status(HttpStatus.OK).build():
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/Mission/quizList/setMissionIsUsed/{idx}")
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

        boolean result = answerMsnService.setMissionIsUsed(idx);
        return (result) ?
                ResponseEntity.status(HttpStatus.OK).build():
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }


    @GetMapping("/Mission/quizList/setMissionIsUsedFalse/{idx}")
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

        boolean result = answerMsnService.setMissionIsUsedFalse(idx);
        return (result) ?
                ResponseEntity.status(HttpStatus.OK).build():
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }


    @GetMapping("/Mission/quizList/setMissionIsView/{idx}")
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

        boolean result = answerMsnService.setMissionIsView(idx);
        return (result) ?
                ResponseEntity.status(HttpStatus.OK).build():
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }


    @GetMapping("/Mission/quizList/setMissionIsViewFalse/{idx}")
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

        boolean result = answerMsnService.setMissionIsViewFalse(idx);
        return (result) ?
                ResponseEntity.status(HttpStatus.OK).build():
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }



    @GetMapping({"/Mission/quizCurrentList/{pageNumber}","/Mission/quizCurrentList","/Mission/quizCurrentList/"})
    public String quizCurrentList(@PathVariable(required = false,value = "pageNumber") Integer pageNumber,HttpSession session, Model model) {
        Member sessionMember = (Member) session.getAttribute("member");
        List<Advertiser> advertisers = advertiserService.getAdvertisers();
        List<Server> servers = serverService.getServers();
        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료
        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }

        ZonedDateTime now = ZonedDateTime.now();
        List<AnswerMsn> answerMsns = answerMsnRepository.findByCurrentList(now);

        // 페이지 번호가 없으면 기본값 1 사용
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }

        // 한 페이지당 최대 10개 데이터
        int limit = 10;
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
        model.addAttribute("advertisers", advertisers);
        model.addAttribute("servers", servers);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "quizCurrentList";


    }

    @Operation(summary = "정답미션 검색", description = "조건에 맞는 정답미션을 검색합니다")
    @PostMapping({"/Mission/quizCurrentList/search","/Mission/quizList/search/"})
    @ResponseBody
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "검색 중 예기치않은 오류발생")
    })
    public Map<String, Object> searchAnswerMsnCurrent(@PathVariable(required = false,value = "pageNumber") Integer pageNumber,HttpSession session, @RequestBody AnswerMsnSearchByConsumedDto dto){
        Member sessionMember= (Member) session.getAttribute("member");
        Map<String, Object> response = new HashMap<>();
        if(sessionMember == null){
            response.put("error", "404"); // 멤버가 없는 경우
            return response;
        } // 세션만료

        Member member =memberRepository.findById( sessionMember.getId());
        if (member == null) {
            response.put("error", "403"); // 비권한 사용자인 경우
            return response;
        }//데이터 없음

        if(member.isNauthAnswerMsn()) { // 비권한 활성화시
            response.put("error", "403");
            return response;
        }


        List<AnswerMsn> result = answerMsnService.searchAnswerMsnCurrent(dto);
        Collections.reverse(result);
        // 페이지 번호가 없으면 기본값 1 사용
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }

        // 한 페이지당 최대 10개 데이터
        int limit = 10;
        int startIndex = (pageNumber - 1) * limit;

        // 전체 리스트의 크기 체크
        List<AnswerMsn> limitedAnswerMsns;
        if (startIndex < result.size()) {
            int endIndex = Math.min(startIndex + limit, result.size());
            limitedAnswerMsns = result.subList(startIndex, endIndex);
        } else {
            limitedAnswerMsns = new ArrayList<>(); // 페이지 번호가 범위를 벗어난 경우 빈 리스트
        }

        int totalPages = (int) Math.ceil((double) result.size() / limit);
        int startPage = ((pageNumber - 1) / limit) * limit + 1; // 현재 페이지 그룹의 시작 페이지
        int endPage = Math.min(startPage + limit - 1, totalPages); // 현재 페이지 그룹의 끝 페이지

        response.put("fullAnswerMsns",result);
        response.put("answerMsns", limitedAnswerMsns);  // limitedMembers 리스트
        response.put("currentPage", pageNumber);  // 현재 페이지 번호
        response.put("totalPages", totalPages);    // 전체 페이지 수
        response.put("startPage",startPage);
        response.put("endPage",endPage);
        return response; // JSON 형태로 반환
    }

    @Operation(summary = "정답미션 검색", description = "조건에 맞는 정답미션을 검색합니다")
    @PostMapping("/Mission/quizCurrentList/search/{pageNumber}")
    @ResponseBody
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "검색 중 예기치않은 오류발생")
    })

    public Map<String, Object> searchAnswerMsnCurrent_page(@PathVariable(required = true,value = "pageNumber") Integer pageNumber,HttpSession session, @RequestBody ResponseDto responseDto){
        Member sessionMember= (Member) session.getAttribute("member");
        Map<String, Object> response = new HashMap<>();
        if(sessionMember == null){
            response.put("error", "404"); // 멤버가 없는 경우
            return response;
        } // 세션만료

        Member member =memberRepository.findById(sessionMember.getId());
        if (member == null) {
            response.put("error", "403"); // 비권한 사용자인 경우
            return response;
        }//데이터 없음

        if(member.isNauthAnswerMsn()) { // 비권한 활성화시
            response.put("error", "403");
            return response;
        }

        List<AnswerMsn> result = responseDto.getInnerAnswerMsns();
        // 페이지 번호가 없으면 기본값 1 사용
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }

        // 한 페이지당 최대 15개 데이터
        int limit = 15;
        int startIndex = (pageNumber - 1) * limit;

        // 전체 리스트의 크기 체크
        List<AnswerMsn> limitedAnswerMsns;
        if (startIndex < result.size()) {
            int endIndex = Math.min(startIndex + limit, result.size());
            limitedAnswerMsns = result.subList(startIndex, endIndex);
        } else {
            limitedAnswerMsns = new ArrayList<>(); // 페이지 번호가 범위를 벗어난 경우 빈 리스트
        }

        int totalPages = (int) Math.ceil((double) result.size() / limit);
        int startPage = ((pageNumber - 1) / limit) * limit + 1; // 현재 페이지 그룹의 시작 페이지
        int endPage = Math.min(startPage + limit - 1, totalPages); // 현재 페이지 그룹의 끝 페이지

        response.put("fullAnswerMsns",result);
        response.put("answerMsns", limitedAnswerMsns);  // limitedMembers 리스트
        response.put("currentPage", pageNumber);  // 현재 페이지 번호
        response.put("totalPages", totalPages);    // 전체 페이지 수
        response.put("startPage",startPage);
        response.put("endPage",endPage);
        return response; // JSON 형태로 반환
    }


    @Operation(summary = "현재 리스트 소진량(정답) 엑셀 다운로드", description = "현재 리스트 소진량(정답) 엑셀파일을 다운로드합니다")
    @GetMapping("/Mission/quizCurrentList/excel/download")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "예기치않은 오류발생")
    })
    public ResponseEntity<Void> excelDownloadCurrent(HttpServletResponse response)throws IOException {
        try (Workbook wb = new XSSFWorkbook()) {
            ZonedDateTime now = ZonedDateTime.now();
            List<AnswerMsn> list = answerMsnRepository.findByCurrentList(now);

            Sheet sheet = answerMsnService.excelDownloadCurrent(list,wb);
            if(sheet !=null) {
                String fileName = URLEncoder.encode("현재 리스트 소진량(정답).xlsx", StandardCharsets.UTF_8);
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

    @GetMapping("/Mission/quizCurrentList/setOffMissionIsUsed/{pageNumber}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러 발생")
    })
    public ResponseEntity<Void> setOffMissionIsUsedCurrent(@PathVariable(required = false,value = "pageNumber") Integer pageNumber,HttpSession session)throws IOException {

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
        // 페이지 번호가 없으면 기본값 1 사용
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        boolean result = answerMsnService.setOffMissionIsUsed(pageNumber);
        return (result) ?
                ResponseEntity.status(HttpStatus.OK).build():
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @PostMapping("/Mission/quizCurrentList/setOffMissionIsUsedSearch/{pageNumber}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러 발생")
    })
    public ResponseEntity<Void> setOffMissionIsUsedCurrent_search(@PathVariable(required = true,value = "pageNumber") Integer pageNumber,HttpSession session,@RequestBody ResponseDto responseDto)throws IOException {

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

        List<AnswerMsn> dto = responseDto.getInnerAnswerMsns();
        boolean result = answerMsnService.setOffMissionIsUsed(pageNumber,dto);

        return (result) ?
                ResponseEntity.status(HttpStatus.OK).build():
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }



    @GetMapping("/Mission/quizCurrentList/setOffMissionIsView/{pageNumber}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러 발생")
    })
    public ResponseEntity<Void> setOffMissionIsViewCurrent(@PathVariable(required = false,value = "pageNumber") Integer pageNumber,HttpSession session)throws IOException {

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
        // 페이지 번호가 없으면 기본값 1 사용
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }

        boolean result = answerMsnService.setOffMissionIsView(pageNumber);
        return (result) ?
                ResponseEntity.status(HttpStatus.OK).build():
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }


    @PostMapping("/Mission/quizCurrentList/setOffMissionIsViewSearch/{pageNumber}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러 발생")
    })
    public ResponseEntity<Void> setOffMissionIsViewCurrent_search(@PathVariable(required = true,value = "pageNumber") Integer pageNumber,HttpSession session,@RequestBody ResponseDto responseDto)throws IOException {

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

        List<AnswerMsn> dto = responseDto.getInnerAnswerMsns();
        boolean result = answerMsnService.setOffMissionIsView(pageNumber,dto);

        return (result) ?
                ResponseEntity.status(HttpStatus.OK).build():
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/Mission/quizCurrentList/AllOffMission")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러 발생")
    })
    public ResponseEntity<Void>AllOffMissionCurrent(HttpSession session)throws IOException {

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

        boolean result = answerMsnService.AllOffMissionCurrent();
        return (result) ?
                ResponseEntity.status(HttpStatus.OK).build():
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }




    @GetMapping("/Mission/quizMultiTempList")
    public String quizMultiTempList(HttpSession session, Model model) {
        Member sessionMember = (Member) session.getAttribute("member");
        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료
        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }

        return "quizMultiTempList";
    }


    @GetMapping("/Mission/quizStaticList/{idx}")
    public String quizReport(@PathVariable(required = true,value = "idx") Integer idx,HttpSession session, Model model) {
        Member sessionMember = (Member) session.getAttribute("member");
        List<Server> servers = serverService.getServers();
        if (sessionMember == null) {
            return "redirect:/actLogout"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료
        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404";
        }

        AnswerMsn answerMsn = answerMsnService.getAnswerMsn(idx);

        LocalDate currentTime = LocalDate.now();
        LocalDate past = LocalDate.now().minusMonths(1);

        List<AnswerMsnDailyStat> answerMsnDailyStat = answerMsnDailyService.getAnswerMsnsDaily(answerMsn.getIdx(),currentTime,past);
        Collections.reverse(answerMsnDailyStat);



        int totalLandingCount = answerMsnDailyStat.stream().mapToInt(AnswerMsnDailyStat::getLandingCnt).sum();  // 랜딩카운트 합
        int totalPartCount =  answerMsnDailyStat.stream().mapToInt(AnswerMsnDailyStat::getPartCnt).sum();  // 참여카운트 합


        model.addAttribute("answerMsn",answerMsn);
        model.addAttribute("answerMsnDailyStat", answerMsnDailyStat);
        model.addAttribute("currentTime",currentTime);
        model.addAttribute("servers",servers);
        model.addAttribute("past",past);
        model.addAttribute("totalLandingCount",totalLandingCount);
        model.addAttribute("totalPartCount",totalPartCount);
        return "quizStaticList";
    }



    @Operation(summary = "정답미션 검색", description = "조건에 맞는 정답미션을 검색합니다")
    @PostMapping("/Mission/quizStaticList/search/{idx}")
    @ResponseBody
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "검색 중 예기치않은 오류발생")
    })
    public Map<String, Object> searchReport(@PathVariable(required = true,value = "idx") Integer idx,HttpSession session, @RequestBody quizStaticListSearchDto dto){
        Member sessionMember= (Member) session.getAttribute("member");
        Map<String, Object> response = new HashMap<>();
        if(sessionMember == null){
            response.put("error", "404"); // 멤버가 없는 경우
            return response;
        } // 세션만료

        Member member =memberRepository.findById( sessionMember.getId());
        if (member == null) {
            response.put("error", "403"); // 비권한 사용자인 경우
            return response;
        }//데이터 없음

        if(member.isNauthAnswerMsn()) { // 비권한 활성화시
            response.put("error", "403");
            return response;
        }

        List<AnswerMsnDailyStat> result = answerMsnService.searchReport(dto,idx);
        Collections.reverse(result);
        // 페이지 번호가 없으면 기본값 1 사용


        response.put("answerMsns", result);
        return response; // JSON 형태로 반환
    }



    @Operation(summary = "엑셀 다운로드", description = "정답미션 리스트 엑셀파일을 다운로드합니다")
    @GetMapping("/Mission/quizStaticList/excel/{idx}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "예기치않은 오류발생")
    })
    public ResponseEntity<Void> reportExcelDownload(@PathVariable(required = true,value = "idx")int idx,HttpServletResponse response)throws IOException {
        try (Workbook wb = new XSSFWorkbook()) {

            LocalDate currentTime = LocalDate.now();
            LocalDate past = LocalDate.now().minusMonths(1);
            List<AnswerMsnDailyStat> list = answerMsnDailyService.getAnswerMsnsDaily(idx,currentTime,past);
            Sheet sheet = answerMsnService.reportExcelDownload(list,wb);
            if(sheet !=null) {
                String fileName = idx +"번 정답 리포트.xlsx";
                fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
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

}
