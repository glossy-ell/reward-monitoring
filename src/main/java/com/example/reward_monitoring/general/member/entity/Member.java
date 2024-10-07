package com.example.reward_monitoring.general.member.entity;


import com.example.reward_monitoring.general.member.model.Auth;
import com.example.reward_monitoring.general.member.model.CtryCode;
import com.example.reward_monitoring.general.member.model.Lang;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@ToString
@EqualsAndHashCode
@Table(name = "members")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "기본키", example = "1")
    private int idx;

    @Column(name = "role")
    @Schema(hidden = true)
    private String role;

    @Comment("아이디")
    @Column(name = "id", nullable = false,unique = true,updatable = false)
    @Schema(description = "ID")
    private String id;

    @Comment("비밀번호")
    @Column(name = "password")
    @Schema(description = "비밀번호")
    private String password;

    @Comment("이름")
    @Column(name = "name", nullable = false,updatable = false)
    @Schema(description = "계정 이름",example = "셀렉티드 총괄 관리자")
    private String name;

    @Comment("부서")
    @Column(name = "department", nullable = false)
    @Schema(description = "부서",example = "셀렉티드 외부 관리 계정")
    private String department;

    @Comment("지역 코드")
    @Enumerated(EnumType.STRING)
    @Column(name="ctry_code")
    @Schema(description = "지역 코드",example = "+82,+852,+1,+81,+86")
    private CtryCode ctryCode;

    @Comment("전화번호")
    @Column(name = "phone", nullable = false)
    @Schema(description = "전화번호",example = "01012345678")
    private String phone;

    @Builder.Default
    @Comment("계정 상태")
    @Column(name = "is_active", nullable = false)
    @Schema(description = "계정상태",example = "true")
    private boolean isActive = true;

    @Comment("마지막 로그인 일시")
    @Column(name = "last_login_at")
    @Schema(description = "마지막 로그인 일시",example = "2024-09-05 12:56:16")
    private ZonedDateTime lastLoginAt;

    @Transient
    private LocalDateTime lastLoginAtLocalDateTime;
    @Transient
    private LocalDate lastLoginAtLocalDate;


    @Enumerated(EnumType.STRING)
    @Column(name="lang")
    @Schema(description = "기본 언어",example = "한국어,English,中文,日本語")
    private Lang lang;

    @Comment("생성 일시")
    @Column(name = "created_at", nullable = false, updatable = false)
    @Schema(description = "계정 생성일시",example = "2024-09-05 12:56:16")
    private ZonedDateTime createdAt;

    @Transient
    private LocalDateTime createdAtLocalDateTime;
    @Transient
    private LocalDate createdAtLocalDate;

    @Comment("정보 수정 일시")
    @Column(name = "edited_at")
    @Schema(description = "정보 수정 일시")
    private ZonedDateTime editedAt;

    @Transient
    private LocalDateTime editedAtLocalDateTime;
    @Transient
    private LocalDate editedAtLocalDate;

    @Comment("관리자 메모")
    @Column(name = "memo")
    @Schema(description = "관리자 메모 ")
    private String memo;

    ///비권한 관리
    ///
    @Builder.Default
    @Comment("관리자 메뉴 목록 비권한 메뉴")
    @Column(name = "nauth_member",nullable = false)
    @Schema(description = "관리자 메뉴 목록 비권한 메뉴 ")
    private boolean nauthMember = false;

    @Builder.Default
    @Comment("정답 미션 목록 비권한 메뉴")
    @Column(name = "nauth_answer_msn",nullable = false)
    @Schema(description = "정답 미션 목록 비권한 메뉴 ")
    private boolean nauthAnswerMsn = false;

    @Builder.Default
    @Comment("현재 리스트 소진량(정답) 비권한 메뉴")
    @Column(name = "nauth_cur_answer",nullable = false)
    @Schema(description = "현재 리스트 소진량(정답) 메뉴 ")
    private boolean nauthCurAnswer = false;

    @Builder.Default
    @Comment("정답 미션 일괄 업로드 비권한 메뉴")
    @Column(name = "nauth_answer_upload",nullable = false)
    @Schema(description = "정답 미션 일괄 업로드 비권한 메뉴")
    private boolean nauthAnswerUpload = false;

    @Builder.Default
    @Comment("검색 미션 목록 비권한 메뉴")
    @Column(name = "nauth_search_msn",nullable = false)
    @Schema(description = "검색 미션 목록 비권한 메뉴 ")
    private boolean nauthSearchMsn = false ;

    @Builder.Default
    @Comment("현재 리스트 소진량(검색) 비권한 메뉴")
    @Column(name = "nauth_cur_search",nullable = false)
    @Schema(description = "현재 리스트 소진량(검색) 메뉴 ")
    private boolean nauthCurSearch = false;

    @Builder.Default
    @Comment("검색 미션 일괄 업로드 비권한 메뉴")
    @Column(name = "nauth_search_upload",nullable = false)
    @Schema(description = "검색 미션 일괄 업로드 비권한 메뉴")
    private boolean nauthSearchUpload = false;

    @Builder.Default
    @Comment("저장 미션 목록 비권한 메뉴")
    @Column(name = "nauth_save_msn",nullable = false)
    @Schema(description = "저장 미션 목록 비권한 메뉴 ")
    private boolean nauthSaveMsn=false;

    @Builder.Default
    @Comment("저장 리스트 소진량(저장) 비권한 메뉴")
    @Column(name = "nauth_cur_save",nullable = false)
    @Schema(description = "현재 리스트 소진량(저장) 메뉴 ")
    private boolean nauthCurSave=false;

    @Builder.Default
    @Comment("저장 미션 일괄 업로드 비권한 메뉴")
    @Column(name = "nauth_save_upload",nullable = false)
    @Schema(description = "저장 미션 일괄 업로드 비권한 메뉴")
    private boolean nauthSaveUpload=false;

    @Builder.Default
    @Comment("정답 미션 데일리 통계 비권한 메뉴")
    @Column(name = "nauth_answer_daily",nullable = false)
    @Schema(description = "정답 미션 데일리 통계 비권한 메뉴 ")
    private boolean nauthAnswerDaily=false;

    @Builder.Default
    @Comment("정답미션별 통계 비권한 메뉴")
    @Column(name = "nauth_answer_detail",nullable = false)
    @Schema(description = "정답미션별 통계 비권한 메뉴")
    private boolean nauthAnswerDetail=false;

    @Builder.Default
    @Comment("정답 미션 일별 합산 통계 비권한 메뉴")
    @Column(name = "nauth_answer_sum",nullable = false)
    @Schema(description = "정답 미션 일별 합산 통계 비권한 메뉴")
    private boolean nauthAnswerSum=false;

    @Builder.Default
    @Comment("검색 미션 데일리 통계 비권한 메뉴")
    @Column(name = "nauth_search_daily",nullable = false)
    @Schema(description = "검색 미션 데일리 통계 비권한 메뉴 ")
    private boolean nauthSearchDaily=false;

    @Builder.Default
    @Comment("검색미션별 통계 비권한 메뉴")
    @Column(name = "nauth_search_detail",nullable = false)
    @Schema(description = "검색미션별 통계 비권한 메뉴")
    private boolean nauthSearchDetail=false;

    @Builder.Default
    @Comment("검색 미션 일별 합산 통계 비권한 메뉴")
    @Column(name = "nauth_search_sum",nullable = false)
    @Schema(description = "검색 미션 일별 합산 통계 비권한 메뉴")
    private boolean nauthSearchSum=false;

    @Builder.Default
    @Comment("저장 미션 데일리 통계 비권한 메뉴")
    @Column(name = "nauth_save_daily",nullable = false)
    @Schema(description = "저장 미션 데일리 통계 비권한 메뉴 ")
    private boolean nauthSaveDaily=false;

    @Builder.Default
    @Comment("저장미션별 통계 비권한 메뉴")
    @Column(name = "nauth_save_detail",nullable = false)
    @Schema(description = "저장미션별 통계 비권한 메뉴")
    private boolean nauthSaveDetail=false;

    @Builder.Default
    @Comment("저장 미션 일별 합산 통계 비권한 메뉴")
    @Column(name = "nauth_save_sum",nullable = false)
    @Schema(description = "저장 미션 일별 합산 통계 비권한 메뉴")
    private boolean nauthSaveSum = false;

    //권한 관리

    @Builder.Default
    @Comment("관리자 목록 권한")
    @Enumerated(EnumType.STRING)
    @Column(name="auth_member")
    @Schema(description = "관리자 목록 권한어",example = "READ,WRITE")
    private Auth authMember =  Auth.READ;

    @Builder.Default
    @Comment("사용자 서버 목록 권한")
    @Enumerated(EnumType.STRING)
    @Column(name="auth_server")
    @Schema(description = "사용자 서버 목록 권한어",example = "READ,WRITE")
    private Auth authServer = Auth.READ;

    @Builder.Default
    @Comment("광고주 목록 권한")
    @Enumerated(EnumType.STRING)
    @Column(name="auth_advertiser")
    @Schema(description = "광고주 목록 권한",example =  "READ,WRITE")
    private Auth authAdvertiser=Auth.READ;

    @Builder.Default
    @Comment("메채사 목록 권한")
    @Enumerated(EnumType.STRING)
    @Column(name="auth_mediacompany")
    @Schema(description = "매체사 목록 권한",example = "READ,WRITE")
    private Auth authMediacompany=Auth.READ;

    @Builder.Default
    @Comment("정답 미션 목록 권한")
    @Enumerated(EnumType.STRING)
    @Column(name = "auth_answer_msn")
    @Schema(description = "정답 미션 목록 권한",example = "READ,WRITE")
    private Auth authAnswerMsn=Auth.READ;

    @Builder.Default
    @Comment("현재 리스트 소진량(정답)권한")
    @Enumerated(EnumType.STRING)
    @Column(name = "auth_cur_answer")
    @Schema(description = "현재 리스트 소진량(정답) 메뉴 ",example = "READ,WRITE")
    private Auth authCurAnswer=Auth.READ;

    @Builder.Default
    @Comment("정답 미션 일괄 업로드 권한")
    @Enumerated(EnumType.STRING)
    @Column(name = "auth_answer_upload")
    @Schema(description = "정답 미션 일괄 업로드 권한",example = "READ,WRITE")
    private Auth authAnswerUpload=Auth.READ;

    @Builder.Default
    @Comment("검색 미션 목록 권한")
    @Enumerated(EnumType.STRING)
    @Column(name = "auth_search_msn")
    @Schema(description = "정답 미션 목록 권한",example = "READ,WRITE")
    private Auth authSearchMsn=Auth.READ;

    @Builder.Default
    @Comment("현재 리스트 소진량(검색)권한")
    @Enumerated(EnumType.STRING)
    @Column(name = "auth_cur_search")
    @Schema(description = "현재 리스트 소진량(정답) 메뉴 ",example = "READ,WRITE")
    private Auth authCurSearch=Auth.READ;

    @Builder.Default
    @Comment("검색 미션 일괄 업로드 권한")
    @Enumerated(EnumType.STRING)
    @Column(name = "auth_search_upload")
    @Schema(description = "검색 미션 일괄 업로드 권한",example = "READ,WRITE")
    private Auth authSearchUpload=Auth.READ;

    @Builder.Default
    @Comment("저장 미션 목록 권한")
    @Enumerated(EnumType.STRING)
    @Column(name = "auth_save_msn")
    @Schema(description = "저장 미션 목록 권한",example = "READ,WRITE")
    private Auth authSaveMsn=Auth.READ;

    @Builder.Default
    @Comment("현재 리스트 소진량(저장)권한")
    @Enumerated(EnumType.STRING)
    @Column(name = "auth_cur_save")
    @Schema(description = "현재 리스트 소진량(저장) 메뉴 ",example = "READ,WRITE")
    private Auth authCurSave=Auth.READ;

    @Builder.Default
    @Comment("저장 미션 일괄 업로드 권한")
    @Enumerated(EnumType.STRING)
    @Column(name = "auth_save_upload")
    @Schema(description = "저장 미션 일괄 업로드 권한",example = "READ,WRITE")
    private Auth authSaveUpload=Auth.READ;

    @Builder.Default
    @Comment("미션 CS 권한")
    @Enumerated(EnumType.STRING)
    @Column(name = "auth_msn_cs")
    @Schema(description = "미션 CS 권한",example = "READ,WRITE")
    private Auth authMsnCS=Auth.READ;

    @Builder.Default
    @Comment("정답 미션 데일리 통계 권한")
    @Enumerated(EnumType.STRING)
    @Column(name = "auth_answer_daily")
    @Schema(description = "정답 미션 목록 권한",example = "READ,WRITE")
    private Auth authAnswerDaily=Auth.READ;

    @Builder.Default
    @Comment("정답 미션별 통계 권한")
    @Enumerated(EnumType.STRING)
    @Column(name = "auth_answer_detail")
    @Schema(description = "정답 미션별 통계 권한 ",example = "READ,WRITE")
    private Auth authAnswerDetail=Auth.READ;

    @Builder.Default
    @Comment("정답 미션 일별 합산 통계 권한")
    @Enumerated(EnumType.STRING)
    @Column(name = "auth_answer_sum")
    @Schema(description = "정답 미션 일별 합산 통계 권한",example = "READ,WRITE")
    private Auth authAnswerSum=Auth.READ;

    @Builder.Default
    @Comment("저장 미션 데일리 통계 권한")
    @Enumerated(EnumType.STRING)
    @Column(name = "auth_save_daily")
    @Schema(description = "저장 미션 목록 권한",example = "READ,WRITE")
    private Auth authSaveDaily=Auth.READ;

    @Builder.Default
    @Comment("저장 미션별 통계 권한")
    @Enumerated(EnumType.STRING)
    @Column(name = "auth_save_detail")
    @Schema(description = "저장 미션별 통계 권한 ",example = "READ,WRITE")
    private Auth authSaveDetail=Auth.READ;

    @Builder.Default
    @Comment("저장 미션 일별 합산 통계 권한")
    @Enumerated(EnumType.STRING)
    @Column(name = "auth_save_sum")
    @Schema(description = "저장 미션 일별 합산 통계 권한",example ="READ,WRITE")
    private Auth authSaveSum=Auth.READ;



    @Builder.Default
    @Comment("검색 미션 데일리 통계 권한")
    @Enumerated(EnumType.STRING)
    @Column(name = "auth_search_daily")
    @Schema(description = "검색 미션 목록 권한",example = "READ,WRITE")
    private Auth authSearchDaily=Auth.READ;

    @Builder.Default
    @Comment("검색 미션별 통계 권한")
    @Enumerated(EnumType.STRING)
    @Column(name = "auth_search_detail")
    @Schema(description = "검색 미션별 통계 권한 ",example = "READ,WRITE")
    private Auth authSearchDetail=Auth.READ;

    @Builder.Default
    @Comment("검색 미션 일별 합산 통계 권한")
    @Enumerated(EnumType.STRING)
    @Column(name = "auth_search_sum")
    @Schema(description = "검색 미션 일별 합산 통계 권한",example = "READ,WRITE")
    private Auth authSearchSum=Auth.READ;




    @PrePersist
    protected void onCreate() {
        createdAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).plusHours(9); //시간 호환 문제
        isActive = true;
    }

    @Builder
    public Member(String id,String password,String name,String department,CtryCode ctryCode,String phone,Lang lang) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.department = department;
        this.ctryCode=ctryCode;
        this.phone=phone;
        this.lang = lang;
    }


    @PostLoad
    public void changeDTypeDateTime(){

        this.createdAtLocalDateTime = this.createdAt.toLocalDateTime().minusHours(9);
        this.createdAtLocalDate = this.createdAt.toLocalDateTime().minusHours(9).toLocalDate();

        if(editedAt !=null) {
            this.editedAtLocalDateTime = this.editedAt.toLocalDateTime().minusHours(9);
            this.editedAtLocalDate = this.editedAt.toLocalDateTime().minusHours(9).toLocalDate();
        }

        if(lastLoginAt != null) {
            this.lastLoginAtLocalDateTime = this.lastLoginAt.toLocalDateTime().minusHours(9);
            this.lastLoginAtLocalDate = this.lastLoginAt.toLocalDateTime().minusHours(9).toLocalDate();
        }
    }
}
