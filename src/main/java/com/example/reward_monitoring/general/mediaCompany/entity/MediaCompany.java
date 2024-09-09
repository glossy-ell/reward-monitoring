package com.example.reward_monitoring.general.mediaCompany.entity;
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

    @Comment("매체사 담당자")
    @Column(name = "company_manager")
    private String companyManager;


    @Comment("매체사 담당자 연락처")
    @Column(name = "company_manage_phone_num")
    private String companyManagePhoneNum;

    @Column(name = "api_key", nullable = false,unique = true)
    private String APIKey;


    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

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
