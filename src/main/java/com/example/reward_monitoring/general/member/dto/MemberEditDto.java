package com.example.reward_monitoring.general.member.dto;


import com.example.reward_monitoring.general.member.model.Auth;
import com.example.reward_monitoring.general.member.model.CtryCode;
import com.example.reward_monitoring.general.member.model.Lang;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.Comment;

@Getter
@Schema(description = "회원 수정 요청 DTO")
public class MemberEditDto {



    @Schema(description = "비밀번호")
    private String password;

    @Schema(description = "계정 이름",example = "셀렉티드 총괄 관리자")
    private String name;

    @Schema(description = "부서",example = "셀렉티드 외부 관리 계정")
    private String department;

    @Schema(description = "지역 코드",example = "+82,+852,+1,+81,+86")
    private CtryCode ctryCode;

    @Schema(description = "전화번호",example = "01012345678")
    private Integer phone;

    @Schema(description = "기본 언어",example = "한국어,English,中文,日本語")
    private Lang lang;

    @Schema(description = "계정상태",example = "true")
    private Boolean isActive = true;

    @Schema(description = "관리자 메모 ")
    private String memo;
    ///하단부턴 비권한 컬럼 (TRUE)일경우 접근불가

    @Schema(description = "관리자 메뉴 목록 비권한 메뉴 ")
    private Boolean nauthMember;


    @Schema(description = "정답 미션 목록 비권한 메뉴 ")
    private Boolean nauthAnswerMsn;

    @Schema(description = "현재 리스트 소진량(정답) 메뉴 ")
    private Boolean nauthCurAnswer;

    @Schema(description = "정답 미션 일괄 업로드 비권한 메뉴")
    private Boolean nauthAnswerUpload;

    @Schema(description = "검색 미션 목록 비권한 메뉴 ")
    private Boolean nauthSearchMsn;

    @Schema(description = "현재 리스트 소진량(검색) 메뉴 ")
    private Boolean nauthCurSearch;

    @Schema(description = "검색 미션 일괄 업로드 비권한 메뉴")
    private Boolean nauthSearchUpload;

    @Schema(description = "저장 미션 목록 비권한 메뉴 ")
    private Boolean nauthSaveMsn;

    @Schema(description = "현재 리스트 소진량(저장) 메뉴 ")
    private Boolean nauthCurSave;

    @Schema(description = "저장 미션 일괄 업로드 비권한 메뉴")
    private Boolean nauthSaveUpload;

    @Schema(description = "정답 미션 데일리 통계 비권한 메뉴 ")
    private Boolean nauthAnswerDaily;

    @Schema(description = "정답미션별 통계 비권한 메뉴")
    private Boolean nauthAnswerDetail;

    @Schema(description = "정답 미션 일별 합산 통계 비권한 메뉴")
    private Boolean nauthAnswerSum;

    @Schema(description = "검색 미션 데일리 통계 비권한 메뉴 ")
    private Boolean nauthSearchDaily;

    @Schema(description = "검색미션별 통계 비권한 메뉴")
    private Boolean nauthSearchDetail;

    @Schema(description = "검색 미션 일별 합산 통계 비권한 메뉴")
    private Boolean nauthSearchSum;

    @Schema(description = "저장 미션 데일리 통계 비권한 메뉴 ")
    private Boolean nauthSaveDaily;

    @Schema(description = "저장미션별 통계 비권한 메뉴")
    private Boolean nauthSaveDetail;

    @Schema(description = "저장 미션 일별 합산 통계 비권한 메뉴")
    private Boolean nauthSaveSum;

    /// 하단부터는 권한 컬럼

    @Schema(description = "관리자 목록 권한어",example = "읽기,쓰기")
    private Auth authMember;

    @Schema(description = "사용자 서버 목록 권한어",example = "읽기,쓰기")
    private Auth authServer;

    @Schema(description = "광고주 목록 권한",example = "읽기,쓰기")
    private Auth authAdvertiser;

    @Schema(description = "매체사 목록 권한",example = "읽기,쓰기")
    private Auth authMediacompany;


    @Schema(description = "정답 미션 목록 권한",example = "읽기,쓰기")
    private Auth authAnswerMsn;

    @Schema(description = "현재 리스트 소진량(정답) 메뉴 ",example = "읽기,쓰기")
    private Auth authCurAnswer;

    @Schema(description = "정답 미션 일괄 업로드 권한",example = "읽기,쓰기")
    private Auth authAnswerUpload;


    @Schema(description = "검색 미션 목록 권한",example = "읽기,쓰기")
    private Auth authSearchMsn;

    @Schema(description = "현재 리스트 소진량(검색) 메뉴 ",example = "읽기,쓰기")
    private Auth authCurSearch;

    @Schema(description = "검색 미션 일괄 업로드 권한",example = "읽기,쓰기")
    private Auth authSearchUpload;


    @Schema(description = "저장 미션 목록 권한",example = "읽기,쓰기")
    private Auth authSaveMsn;

    @Schema(description = "현재 리스트 소진량(저장) 메뉴 ",example = "읽기,쓰기")
    private Auth authCurSave;

    @Schema(description = "저장 미션 일괄 업로드 권한",example = "읽기,쓰기")
    private Auth authSaveUpload;


    @Schema(description = "미션 CS 권한",example = "읽기,쓰기")
    private Auth authMsnCS;


    @Schema(description = "정답 미션 목록 권한",example = "읽기,쓰기")
    private Auth authAnswerDaily;

    @Schema(description = "정답 미션별 통계 권한 ",example = "읽기,쓰기")
    private Auth authAnswerDetail;

    @Schema(description = "정답 미션 일별 합산 통계 권한",example = "읽기,쓰기")
    private Auth authAnswerSum;


    @Schema(description = "정답 미션 목록 권한",example = "읽기,쓰기")
    private Auth authSaveDaily;

    @Schema(description = "정답 미션별 통계 권한 ",example = "읽기,쓰기")
    private Auth authSaveDetail;

    @Schema(description = "정답 미션 일별 합산 통계 권한",example = "읽기,쓰기")
    private Auth authSaveSum;


    @Schema(description = "정답 미션 목록 권한",example = "읽기,쓰기")
    private Auth authSearchDaily;

    @Schema(description = "검색 미션별 통계 권한 ",example = "읽기,쓰기")
    private Auth authSearchDetail;

    @Schema(description = "검색 미션 일별 합산 통계 권한",example = "읽기,쓰기")
    private Auth authSearchSum;


}
