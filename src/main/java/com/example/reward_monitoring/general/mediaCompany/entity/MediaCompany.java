package com.example.reward_monitoring.general.mediaCompany.entity;
import com.example.reward_monitoring.general.mediaCompany.model.Type;
import com.example.reward_monitoring.general.member.model.Lang;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@ToString
@Table(name = "mediacompanys")

public class MediaCompany {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idx;

    @Column(name = "company_name", nullable = false)
    private  String companyName;

    @Comment("매체사 ID")
    @Column(name = "company_id", nullable = false ,unique = true)
    private  String companyID;

    @Comment("매체사  비밀번호")
    @Column(name = "company_password", nullable = false )
    private  String password;

    @Comment("매체사 담당자")
    @Column(name = "company_manager")
    private String companyManager;


    @Comment("매체사 담당자 연락처")
    @Column(name = "company_manage_phone_num")
    private String companyManagePhoneNum;

    @Column(name = "api_key", nullable = false,unique = true)
    private String APIKey;

    @Comment("생성일시")
    @Column(name = "is_active", nullable = false)
    @Schema(description = "생성일시")
    private boolean isActive;

    @Comment("생성일시")
    @Column(name = "created_at", nullable = false, updatable = false)
    @Schema(description = "생성일시")
    private ZonedDateTime createdAt;
    
    @Enumerated(EnumType.STRING)
    @Column(name="type") // 개발 , 운영
    @Schema(description = "운영 타입 ",example = "개발,운영")
    private Type type;


    @Comment("매체사 리턴 URL")
    @Column(name = "company_return_url",unique = true)
    @Schema(description = "매체사 리턴 URL ")
    private  String companyReturnUrl;

    @Comment("매체사 리턴 파라미터")
    @Column(name = "company_return_parameter" ,unique = true)
    @Schema(description = "매체사 리턴 파라미터 ")
    private  String companyReturnParameter;

    @Comment("매체사 유저 적립금")
    @Column(name = "company_user_saving" ,unique = true)
    @Schema(description = "매체사 유저 적립금 ")
    private  int companyUserSaving;

    @Comment("관리자 메모")
    @Column(name = "memo")
    @Schema(description = "관리자 메모 ")
    private String memo;


    @PrePersist
    protected void onCreate() {
        createdAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
    }


    @Builder
    public MediaCompany(String companyName,String companyID,String companyManager,String companyManagePhoneNum,String APIKey) {
    this.companyName = companyName;
    this.companyID = companyID;
    this.companyManager = Objects.requireNonNullElse(companyManager, "미정");
    this.companyManagePhoneNum= Objects.requireNonNullElse(companyManagePhoneNum, "미정");
    this.APIKey = APIKey;

    }
}
