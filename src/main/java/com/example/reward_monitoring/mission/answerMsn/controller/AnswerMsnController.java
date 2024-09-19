package com.example.reward_monitoring.mission.answerMsn.controller;


import com.example.reward_monitoring.general.member.entity.Member;
import com.example.reward_monitoring.general.member.model.Auth;
import com.example.reward_monitoring.general.member.repository.MemberRepository;
import com.example.reward_monitoring.mission.answerMsn.dto.*;
import com.example.reward_monitoring.mission.answerMsn.entity.AnswerMsn;
import com.example.reward_monitoring.mission.answerMsn.repository.AnswerMsnRepository;
import com.example.reward_monitoring.mission.answerMsn.service.AnswerMsnService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@Tag(name = "AnswerMsn", description = "정답미션관리 API")
@RequestMapping("/Mission/quizList")
public class AnswerMsnController {
    @Autowired
    private AnswerMsnRepository answerMsnRepository;

    @Autowired
    private AnswerMsnService answerMsnService;

    @Autowired
    private MemberRepository memberRepository;

    @Operation(summary = "정답미션 정보 수정", description = "정답미션 정보를 수정합니다")
    @Parameter(name = "idx", description = "수정할 정답미션의 IDX")
    @PostMapping("/edit/{idx}")
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
                                          HttpServletResponse response){

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
        AnswerMsn edited = answerMsnService.edit(idx,dto);

        if(edited == null){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        answerMsnRepository.save(edited);
        return ResponseEntity.status(HttpStatus.OK).body(edited);
    }


    @Operation(summary = "정답미션 생성", description = "정답미션 정보를 생성합니다")
    @PostMapping("/add")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 수정됨"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<Void> add(HttpSession session,
            @RequestPart(value ="file",required = false)MultipartFile multipartFile,
            @RequestPart(value ="dto",required = true)AnswerMsnReadDto dto){

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

        answerMsnRepository.save(created);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }




    @Operation(summary = "정답미션 정보 요청", description = "IDX와 일치하는 미션정보를 반환합니다")
    @Parameter(name = "idx", description = "관리자 IDX")
    @GetMapping("/{idx}")
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
    @GetMapping("/answerMsns")
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
    @DeleteMapping("/delete/{idx}")
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

    @Operation(summary = "정답미션 검색", description = "조건에 맞는 정답미션을 검색합니다")
    @PostMapping("/search")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "검색 중 예기치않은 오류발생")
    })
    public ResponseEntity<List<AnswerMsn>> searchAnswerMsn(HttpSession session, @RequestBody AnswerMsnSearchDto dto){
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


        List<AnswerMsn> result = answerMsnService.searchAnswerMsn(dto);
        return (result != null) ?
                ResponseEntity.status(HttpStatus.OK).body(result): // 일치하는 결과가 없을경우 빈 리스트 반환
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }


    @Operation(summary = "정답미션 현재 리스트 소진량 검색", description = "현재 리스트 소진량 페이지에서 조건에 맞는 정답미션을 검색합니다")
    @PostMapping("/searchMsnByConsumed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 완료(조건에 맞는결과가없을경우 빈 리스트 반환)"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "검색 중 예기치않은 오류발생")
    })
    public ResponseEntity<List<AnswerMsn>> searchAnswerMsnByConsumed(HttpSession session,@RequestBody AnswerMsnSearchDto dto){
        Member sessionMember= (Member) session.getAttribute("member");
        if(sessionMember == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } // 세션만료

        Member member =memberRepository.findById( sessionMember.getId());
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }//데이터 없음

        if(member.isNauthCurAnswer()) // 비권한 활성화시
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();


        List<AnswerMsn> result = answerMsnService.searchAnswerMsnByConsumed(dto);
        return (result != null) ?
                ResponseEntity.status(HttpStatus.OK).body(result): // 일치하는 결과가 없을경우 빈 리스트 반환
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @Operation(summary = "모든 미션 종료", description = "!!DB의 모든 미션을 종료하고 비노출처리합니다!!")
    @GetMapping("/endAll")
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

        if(member.isNauthAnswerMsn()) // 비권한 활성화시
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        if(member.getAuthAnswerMsn()== Auth.READ) // 읽기 권한만 존재할경우
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();


        boolean result = answerMsnService.allMissionEnd();

        return (result) ?
                ResponseEntity.status(HttpStatus.OK).build():
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }


    @Operation(summary = "엑셀 다운로드", description = "정답미션 리스트 엑셀파일을 다운로드합니다")
    @GetMapping("/excel/download")
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
    @PostMapping("/excel/upload")
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
        boolean result = answerMsnService.readExcel(file);

        return (result) ?
                ResponseEntity.status(HttpStatus.OK).build():
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }



    @Operation(summary = "미션 사용여부 변경", description = "미션 사용여부를 변경합니다")
    @PostMapping("/reEngagement/{idx}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "403", description = "권한없음"),
            @ApiResponse(responseCode = "500", description = "")
    })
    public ResponseEntity<Void> changeMissionActive(HttpSession session, @PathVariable int idx , @RequestBody AnswerMsnActiveDto dto)throws IOException {

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


        boolean result = answerMsnService.changeMissionActive(idx,dto);

        return (result) ?
                ResponseEntity.status(HttpStatus.OK).build():
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @Operation(summary = "미션 노출여부 변경", description = "미션 노출여부를 변경합니다.")
    @PostMapping("/expose/{idx}")
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
    @PostMapping("/{idx}/changeDay")
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


    @GetMapping("/")
    public String quizList(HttpSession session){
//        Member sessionMember= (Member) session.getAttribute("member");
//        if(sessionMember == null){
//            return "redirect:/loginForm";
//        } // 세션만료
//
//        Member member =memberRepository.findById( sessionMember.getId());
//        if (member == null) {
//            return "redirect:/loginForm";
//        }//데이터 없음
//
//        if(member.isNauthAnswerMsn()) // 비권한 활성화시
//            return "redirect:/accessDenied";


        return "quizList";
    }

    @GetMapping("/Mission/quizCurrentList")
    public void quizCurrentList(HttpSession session, HttpServletResponse response) throws IOException {
//        // 권한 검사 로직 (예시)
//        if (!hasPermission()) {
//            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized"); // 401 오류를 반환
//            return;
//        }
//
//        // 권한이 있을 경우 다른 페이지로 리다이렉트
//        response.sendRedirect("/anotherPage"); // 이동할 URL
    }
}
