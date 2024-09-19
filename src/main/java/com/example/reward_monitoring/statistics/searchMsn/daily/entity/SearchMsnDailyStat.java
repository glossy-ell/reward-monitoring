package com.example.reward_monitoring.statistics.searchMsn.daily.entity;


import com.example.reward_monitoring.general.advertiser.entity.Advertiser;
import com.example.reward_monitoring.general.mediaCompany.entity.MediaCompany;
import com.example.reward_monitoring.general.userServer.entity.Server;
import com.example.reward_monitoring.mission.searchMsn.entity.SearchMsn;
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
@Table(name = "search_msn_daily_stat")
public class SearchMsnDailyStat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "기본키", example = "1")
    private int idx;

    @Comment("매체사 IDX")
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumns({
            @JoinColumn(name = "mediaCompany_idx", referencedColumnName = "idx"),
            @JoinColumn(name = "company_name", referencedColumnName = "company_name")
    })
    @Schema(description = "매체사 IDX", example = "3")
    MediaCompany mediaCompany;

    @Comment("광고주 정보)")
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumns({
            @JoinColumn(name = "advertiser_idx", referencedColumnName = "idx"),
            @JoinColumn(name = "advertiser", referencedColumnName = "advertiser")
    })
    @Schema(description = "광고주 정보")
    Advertiser advertiser;

    @Comment("미션 정보")
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumns({
            @JoinColumn(name = "advertiser_details", referencedColumnName = "advertiser_details"),
            @JoinColumn(name = "answer_msn_idx", referencedColumnName = "idx"),
            @JoinColumn(name = "mission_title" , referencedColumnName =  "mission_title")
    })
    @Schema(description = "정답 미션 IDX", example = "3")
    SearchMsn searchMsn;

    @Comment("서버URL(외래키)")
    @ManyToOne(cascade=CascadeType.REMOVE)
    @JoinColumn(name="server_url", referencedColumnName = "server_url")
    @Schema(description = "서버URL(외래키)")
    Server server;


    @Comment("랜딩 카운트")
    @Column(name = "total_landing_cnt")
    @Schema(description = "전체 랜딩수")
    private int landingCnt;

    @Comment("참여 카운트")
    @Column(name = "total_part_cnt")
    @Schema(description = "참여 카운트")
    private int partCnt;

    @Comment("참여일")
    @Column(name = "part_date", nullable = false, updatable = false)
    @Schema(description = "참여일", example = "2024-09-11")
    private LocalDate partDate;


    @Builder
    public SearchMsnDailyStat(MediaCompany mediaCompany, Advertiser advertiser, SearchMsn searchMsn
            , int landingCnt, int partCnt, LocalDate partDate, Server server) {

        this.mediaCompany = mediaCompany;
        this.advertiser = advertiser;
        this.searchMsn = searchMsn;
        this.landingCnt = landingCnt;
        this.partCnt = partCnt;
        this.partDate = partDate;
        this.server = server;

    }
}