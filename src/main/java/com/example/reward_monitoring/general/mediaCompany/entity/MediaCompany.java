package com.example.reward_monitoring.general.mediaCompany.entity;

import com.example.reward_monitoring.general.mediaCompany.model.Type;
import com.example.reward_monitoring.general.userServer.entity.Server;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
@EqualsAndHashCode
@Table(name = "mediacompanys")
public class MediaCompany {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idx;

    @Comment("매체사명")
    @Column(name = "company_name", nullable = false)
    @Schema(description = "매체사명")
    private  String companyName;

    @Comment("사용자 서버(외래키)")
    @ManyToOne()
    @JoinColumn(name="server_url", referencedColumnName = "server_url" , nullable = false )
    @Schema(description = "사용자 서버", example = "https://ocb.srk.co.kr")
    Server server;

    @Comment("매체사 ID")
    @Column(name = "company_id", nullable = false ,unique = true)
    @Schema(description = "매체사아이디")
    private  String companyID;

    @Comment("매체사  비밀번호")
    @Column(name = "company_password", nullable = false )
    @Schema(description = "매체사 비밀번호")
    private String password;

    @Builder.Default
    @Comment("매체사 담당자")
    @Column(name = "company_manager")
    @Schema(description = "매체사 담당자")
    private String companyManager="미정";


    @Builder.Default
    @Comment("매체사 담당자 연락처")
    @Column(name = "company_manage_phone_num")
    @Schema(description = "매체사 담당자 연락처")
    private String companyManagePhoneNum="미정";

    @Comment("API Key")
    @Column(name = "api_key", nullable = false,unique = true)
    @Schema(description = "API Key")
    private String APIKey; //어떻게 생성?

    @Builder.Default
    @Comment("매체사 사용 여부")
    @Column(name = "is_active", nullable = false)
    @Schema(description = "매체사 사용 여부")
    private boolean isActive = false;

    @Comment("생성일시")
    @Column(name = "created_at", nullable = false, updatable = false)
    @Schema(description = "생성일시")
    private ZonedDateTime createdAt;

    @Transient
    private LocalDateTime createdAtLocalDateTime;
    @Transient
    private LocalDate createdAtLocalDate;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Comment("매체사 개발/운영")
    @Column(name="type") // 개발 , 운영
    @Schema(description = "매체사 개발/운영",example = "develop,operate")
    private Type type = Type.operate;


    @Comment("매체사 리턴 URL")
    @Column(name = "company_return_url")
    @Schema(description = "매체사 리턴 URL ")
    private  String companyReturnUrl;

    @Comment("매체사 리턴 파라미터")
    @Column(name = "company_return_parameter")
    @Schema(description = "매체사 리턴 파라미터 ")
    private String companyReturnParameter;

    @Comment("매체사 유저 적립금-정답미션")
    @Column(name = "company_user_saving_quiz")
    @Schema(description = "매체사 유저 적립금-정답미션")
    private  int companyUserSavingQuiz;


    @Comment("매체사 유저 적립금-검색미션")
    @Column(name = "company_user_saving_search")
    @Schema(description = "매체사 유저 적립금-검색미션")
    private  int companyUserSavingSearch;

    @Comment("매체사 유저 적립금-저장미션")
    @Column(name = "company_user_saving_sightseeing")
    @Schema(description = "매체사 유저 적립금-저장미션")
    private  int companyUserSavingSightseeing;

    @Comment("관리자 메모")
    @Column(name = "memo")
    @Schema(description = "관리자 메모 ")
    private String memo;



    @PrePersist
    protected void onCreate() {
        createdAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
    }


    @Builder
    public MediaCompany(String companyName,Server server,String companyID,String password,String companyManager,String companyManagePhoneNum,String APIKey
    ,boolean isActive,Type type,String companyReturnUrl,String companyReturnParameter,String memo,int companyUserSavingQuiz,
                        int companyUserSavingSearch, int companyUserSavingSightseeing) {
    this.companyName = companyName;
    this.server = server;
    this.companyID = companyID;
    this.password = password;
    this.companyManager = companyManager;
    this.companyManagePhoneNum= companyManagePhoneNum;
    this.APIKey = APIKey;
    this.isActive = isActive;
    this.type = type;
    this.companyReturnUrl = companyReturnUrl;
    this.companyReturnParameter = companyReturnParameter;
    this.memo = memo;
    this.companyUserSavingQuiz = companyUserSavingQuiz;
    this.companyUserSavingSearch = companyUserSavingSearch;
    this.companyUserSavingSightseeing = companyUserSavingSightseeing;

    }

    @PostLoad
    public void changeDTypeDateTime(){
        this.createdAtLocalDateTime = this.createdAt.toLocalDateTime();
        this.createdAtLocalDate = this.createdAt.toLocalDate();

    }
}
