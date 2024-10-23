package com.example.reward_monitoring.statistics.saveMsn.sum.entity;

import com.example.reward_monitoring.general.advertiser.entity.Advertiser;
import com.example.reward_monitoring.general.mediaCompany.entity.MediaCompany;
import com.example.reward_monitoring.general.userServer.entity.Server;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "save_msn_sum_stat")
public class SaveMsnSumStat {

    @Comment("광고주 정보)")
    @ManyToOne()
    @JoinColumns({
            @JoinColumn(name = "advertiser_idx", referencedColumnName = "idx"),
            @JoinColumn(name = "advertiser", referencedColumnName = "advertiser"),
    })
    @Schema(description = "광고주 정보")
    Advertiser advertiser;

    @Comment("매체사 IDX")
    @ManyToOne()
    @JoinColumns({
            @JoinColumn(name = "mediaCompany_idx", referencedColumnName = "idx"),
            @JoinColumn(name = "company_name", referencedColumnName = "company_name")
    })
    @Schema(description = "매체사 IDX", example = "3")
    MediaCompany mediaCompany;

    @Comment("서버URL(외래키)")
    @ManyToOne()
    @JoinColumn(name="server_url", referencedColumnName = "server_url")
    @Schema(description = "서버URL(외래키)")
    Server server;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "기본키", example = "1")
    private int idx;

    @Builder.Default
    @Comment("일시")
    @Column(name = "date", nullable = false, updatable = false)
    @Schema(description = "일시", example = "2024-09-11")
    private LocalDate date = LocalDate.now();

    @Comment("각 랜딩 카운트")
    @Column(name = "landing_cnt")
    @Schema(description = "랜딩수")
    private int landingCnt;

    @Comment("각 참여 카운트")
    @Column(name = "part_cnt")
    @Schema(description = "카운트")
    private int partCnt;



    @Builder
    public SaveMsnSumStat(LocalDate date, int landingCnt, int partCnt) {
        this.date = date;
        this.landingCnt= landingCnt;
        this.partCnt= partCnt;
    }
}
