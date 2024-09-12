package com.example.reward_monitoring.statistics.answerMsnStat.daily.entity;


import com.example.reward_monitoring.general.advertiser.entity.Advertiser;
import com.example.reward_monitoring.general.mediaCompany.entity.MediaCompany;
import com.example.reward_monitoring.mission.answerMsn.entity.AnswerMsn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@ToString
@Table(name = "answer_msn_daily_stat")
public class AnswerMsnDailyStat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "기본키", example = "1")
    private int idx;

    @Comment("매체사 IDX")
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumns({
            @JoinColumn(name = "mediaCompany_idx", referencedColumnName = "idx", nullable = false),
            @JoinColumn(name = "mediaCompany_idx", referencedColumnName = "companyName", nullable = false),
    })

    @JoinColumn(name = "mediaCompany_idx", referencedColumnName = "idx", nullable = false)
    @Schema(description = "매체사 IDX", example = "3")
    MediaCompany mediaCompany;

    @Comment("광고주  정보)")
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumns({
            @JoinColumn(name = "advertiser", referencedColumnName = "advertiser", nullable = false)
    })
    @Schema(description = "광고주 정보")
    Advertiser advertiser;



    @Comment("광고주 상세")
    @Column(name = "advertiser_details")
    @Schema(description = "광고주 상세")
    private String advertiserDetails;

    @Comment("미션 정보")
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumns({
            @JoinColumn(name = "msn_idx", referencedColumnName = "idx", nullable = false),
            @JoinColumn(name = "msn_title", referencedColumnName = "mission_title", nullable = false)
    })
    @Schema(description = "미션 정보")
    AnswerMsn answerMsn;

    @Builder.Default
    @Comment("랜딩 카운트")
    @Column(name = "total_landing_cnt")
    @Schema(description = "전체 랜딩수")
    private int landingCnt = 0;

    @Builder.Default
    @Comment("참여 카운트")
    @Column(name = "total_part_cnt")
    @Schema(description = "참여 카운트")
    private int partCnt = 0;

    @Comment("참여일")
    @Column(name = "part_date", nullable = false, updatable = false)
    @Schema(description = "참여일", example = "2024-09-11")
    private LocalDate partDate;

    @Builder
    public AnswerMsnDailyStat(MediaCompany mediaCompany, Advertiser advertiser, AnswerMsn answerMsn
            , int landingCnt, int partCnt, LocalDate partDate) {

        this.mediaCompany = mediaCompany;
        this.advertiser = advertiser;
        this.answerMsn = answerMsn;
        this.landingCnt = landingCnt;
        this.partCnt = partCnt;
        this.partDate = partDate;


    }
}