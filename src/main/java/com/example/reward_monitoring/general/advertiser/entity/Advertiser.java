package com.example.reward_monitoring.general.advertiser.entity;

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
@Table(name = "advertisers")

public class Advertiser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "기본키", example = "1")
    private int idx;

    @Comment("광고주명")
    @Column(name = "advertiser", nullable = false,unique=true)
    @Schema(description = "광고주명")
    private String advertiser;

    @Comment("광고주 담당자")
    @Column(name = "manager")
    @Schema(description = "광고주 담당자")
    private String manager;

    @Comment("광고주 담당자 연락처")
    @Column(name = "manager_phone_num")
    @Schema(description = "광고주 담당자 연락처")
    private String managerPhoneNum;

    @Builder.Default
    @Comment("활성여부")
    @Column(name = "is_active", nullable = false)
    @Schema(description = "활성여부")
    private boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Schema(description = "생성시간")
    private ZonedDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
    }

    @Builder
    public Advertiser(String advertiser,String manager,String managerPhoneNum) {
        this.advertiser = advertiser;
        this.manager = Objects.requireNonNullElse(manager, "미정");
        this.managerPhoneNum = Objects.requireNonNullElse(managerPhoneNum, "미정");
    }
}
