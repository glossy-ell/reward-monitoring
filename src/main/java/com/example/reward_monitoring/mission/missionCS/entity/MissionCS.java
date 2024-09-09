package com.example.reward_monitoring.mission.missionCS.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

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

    @Comment("미션 타입")
    @Column(name = "mission_type",nullable = false)
    @Schema(description = "미션 타입", example = "50")
    private String missionType;

    @Comment("미션 제목")
    @Column(name ="mission_title",nullable = false)
    @Schema(description = "미션 제목", example = "42880 / 욕실선반")
    private String missionTitle;

    @Builder.Default
    @Comment("문의 카운트")
    @Column(name = "cs_count",nullable = false)
    @Schema(description = "문의 카운트", example = "1")
    private int CSCount=1;


    @Comment("cs 유형")
    @Column(name = "cs_type",nullable = false)
    @Schema(description = "cs 유형", example = "참여하기 URL 확인")
    private String csType;

    @Comment("최초 등록 일시")
    @Column(name = "first_reg_date")
    @Schema(description = "최초 등록 일시", example = "2024-09-05 16:19:55")
    private ZonedDateTime firstRegDate;

    @Builder
    public MissionCS(String missionType,String missionTitlet,String csType,ZonedDateTime firstRegDate){
        this.missionType = missionType;
        this.missionTitle = missionTitle;
        this.csType = csType;
        this.firstRegDate = firstRegDate;
    }


}
