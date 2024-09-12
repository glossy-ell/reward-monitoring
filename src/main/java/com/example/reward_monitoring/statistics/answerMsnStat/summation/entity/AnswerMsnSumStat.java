package com.example.reward_monitoring.statistics.answerMsnStat.summation.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class AnswerMsnSumStat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "기본키", example = "1")
    private int idx;

    @Builder.Default
    @Comment("일시")
    @Column(name = "date", nullable = false, updatable = false)
    @Schema(description = "일시", example = "2024-09-11")
    private LocalDate date = LocalDate.now();

    @Builder.Default
    @Comment("랜딩 카운트")
    @Column(name = "total_landing_cnt")
    @Schema(description = "전체 랜딩수")
    int landingCount = 0;

    @Builder.Default
    @Comment("참여 카운트")
    @Column(name = "total_part_cnt")
    @Schema(description = "참여 카운트")
    int partCount =0;

    @Builder
    public AnswerMsnSumStat(LocalDate date, int landingCount, int partCount) {
        this.date = date;
        this.landingCount = landingCount;
        this. partCount = partCount;
    }
}
