package com.example.reward_monitoring.general.member.entity;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;

import java.time.ZoneId;
import java.time.ZonedDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@ToString
@Table(name = "members")
@Slf4j
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "기본키", example = "1")
    private int idx;

    @Column(name = "role")
    @Schema(hidden = true)
    private String role;

    @Column(name = "id", nullable = false,unique = true,updatable = false)
    @Schema(description = "ID")
    private String id;

    @Column(name = "password")
    @Schema(description = "비밀번호")
    private String password;

    @Column(name = "name", nullable = false,updatable = false)
    @Schema(description = "계정 이름",example = "셀렉티드 총괄 관리자")
    private String name;

    @Column(name = "department", nullable = false)
    @Schema(description = "부서",example = "셀렉티드 외부 관리 계정")
    private String department;


    @Column(name = "phone", nullable = false)
    @Schema(description = "전화번호",example = "01012345678")
    private int phone;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    @Schema(description = "계정상태",example = "true")
    private boolean isActive = true;

    @Comment("마지막 로그인 일시")
    @Column(name = "last_login_at")
    @Schema(description = "마지막 로그인 일시",example = "2024-09-05 12:56:16")
    private ZonedDateTime lastLoginAt;

    @Comment("생성 일시")
    @Column(name = "created_at", nullable = false, updatable = false)
    @Schema(description = "계정 생성일시",example = "2024-09-05 12:56:16")
    private ZonedDateTime createdAt;

    @Comment("정보 수정 일시")
    @Column(name = "edited_at")
    @Schema(hidden = true)
    private ZonedDateTime editedAt;


    @PrePersist
    protected void onCreate() {
        createdAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).plusHours(9); //시간 호환 문제
        log.info(String.valueOf(createdAt));
        isActive = true;
    }

    @Builder
    public Member(String id,String password,String name,String department) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.department = department;
    }
}
