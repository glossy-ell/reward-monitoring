package com.example.reward_monitoring.mission.answerMsn.entity;


import com.example.reward_monitoring.general.advertiser.entity.Advertiser;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@ToString
@Table(name = "answer_msn")
public class AnswerMsn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "기본키", example = "1")
    private int idx;

    @Comment("미션 기본 수량")
    @Column(name = "mission_default_qty", nullable = false)
    @Schema(description = "미션 기본 수량", example = "50")
    private int missionDefaultQty;

    @Comment("미션 데일리 캡")
    @Column(name = "mission_daily_cap", nullable = false)
    @Schema(description = "미션 데일리 캡", example = "50")
    private int missionDailyCap;

    @Comment("광고주(외래키)")
    @ManyToOne(cascade=CascadeType.REMOVE)
    @JoinColumn(name="advertiser", referencedColumnName = "advertiser" , nullable = false )
    @Schema(description = "광고주", example = "원픽")
    Advertiser advertiser;
    
    @Comment("광고주 상세")
    @Column(name = "advertiser_details")
    @Schema(description = "광고주 상세", example = "82652333318")
    private String advertiserDetails;
    
    @Comment("미션 제목")
    @Column(name = "mission_title",nullable = false)
    @Schema(description = "미션 제목", example = "무쇠웍")
    private String missionTitle;

    @Comment("미션 정답")
    @Column(name = "mission_answer",nullable = false)
    @Schema(description = "미션 정답", example = "5107811272")
    private String missionAnswer;
    


    @Comment("미션 시작일시")
    @Column(name = "start_at_msn", nullable = false, updatable = false)
    @Schema(description = "미션 시작일시", example = "2024-09-04 15:00:00")
    private ZonedDateTime startAtMsn;

    @Comment("미션 종료일시")
    @Column(name = "end_at_msn", nullable = false, updatable = false)
    @Schema(description = "미션 종료일시", example = "2024-09-13 23:40:00")
    private ZonedDateTime endAtMsn;

    @Comment("데일리캡 시작일시")
    @Column(name = "start_at", nullable = false, updatable = false)
    @Schema(description = "데일리캡 시작일시", example = "2024-09-04 ")
    private LocalDate startAtCap;

    @Comment("데일리캡 종료일시")
    @Column(name = "end_at", nullable = false, updatable = false)
    @Schema(description = "데일리캡 종료일시", example = "2024-09-13")
    private LocalDate endAtCap;

    @Builder.Default
    @Comment("미션 사용여부")
    @Column(name = "mission_active", nullable = false)
    @Schema(description = "미션 사용여부", example = "true")
    private boolean missionActive =true;
    
    @Comment("미션 노출여부")
    @Column(name = "mission_exp", nullable = false)
    @Schema(description = "미션 노출여부", example = "true")
    private boolean missionExposure;
    
    @Comment("중복 참여 가능 여부(+1 Day)")
    @Column(name = "dup_participation", nullable = false)
    @Schema(description = "중복 참여 가능여부", example = "true")
    private boolean dupParticipation;

    @Comment("재참여 가능일")
    @Column(name = "re_engagementDay" )
    @Schema(description = "재참여 가능일", example = "1")
    private int reEngagementDay;

    @Comment("전체 랜딩수")
    @Column(name = "total_landing_cnt")
    @Schema(description = "전체 랜딩수")
    private int totalLandingCnt;

    @Comment("전체 참여수")
    @Column(name = "전체 참여수")
    private int totalPartCnt;

    @Builder.Default
    @Comment("미션 데이터 타입")  // false = 삭제 데이터 , true = 정상 데이터
    @Column(name = "data_type")
    private boolean dataType = true;

    @Builder
    public AnswerMsn(int missionDefaultQty,int missionDailyCap,Advertiser advertiser,String advertiserDetails
    ,String missionTitle,String missionAnswer,ZonedDateTime startAtMsn,ZonedDateTime endAtMsn,LocalDate  startAtCap
            ,LocalDate  endAtCap,boolean missionExposure,boolean dupParticipation,int reEngagementDay) {
        this.missionDefaultQty = missionDefaultQty;
        this.missionDailyCap = missionDailyCap;
        this.advertiser = advertiser;
        this.advertiserDetails = advertiserDetails;
        this.missionTitle = missionTitle;
        this.missionAnswer = missionAnswer;
        this.startAtMsn = startAtMsn;
        this.endAtMsn = endAtMsn;
        this.startAtCap = startAtCap;
        this.endAtCap = endAtCap;
        this.missionExposure = missionExposure;
        this.dupParticipation = dupParticipation;
        this.reEngagementDay = reEngagementDay;

    }
}


