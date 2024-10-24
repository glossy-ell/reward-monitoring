package com.example.reward_monitoring.mission.missionCS.entity;


import com.example.reward_monitoring.mission.missionCS.model.CSType;
import com.example.reward_monitoring.mission.missionCS.model.MsnType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@ToString
@Table(name = "mission_cs")
public class MissionCS {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idx;

    @Enumerated(EnumType.STRING)
    @Column(name="msn_type")
    @Schema(description = "미션 타입",example = "정답,검색,저장")
    private MsnType msnType;

    @Comment("미션 제목")
    @Column(name = "msn_title")
    @Schema(description = "미션 제목", example = "42871 / 거리측정기")
    private String msnTitle;

    @Comment("문의 카운트")
    @Column(name = "cs_count",nullable = false)
    @Schema(description = "문의 카운트", example = "1")
    private int CSCount;


    @Enumerated(EnumType.STRING)
    @Column(name = "cs_type",nullable = false)
    @Schema(description = "CS 타입", example = "정답제목확인,URL확인,데이터유실")
    private CSType csType;

    @Comment("최초 등록 일시")
    @Column(name = "first_reg_date")
    @Schema(description = "최초 등록 일시", example = "2024-09-05 16:19:55")
    private LocalDateTime firstRegDate;

    @Builder
    public MissionCS(MsnType msnType,String msnTitle,CSType csType,LocalDateTime firstRegDate){
        this.msnType = msnType;
        this.msnTitle = msnTitle;
        this.csType = csType;
        this.firstRegDate = firstRegDate;
    }


}
