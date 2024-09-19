package com.example.reward_monitoring.mission.missionCS.dto;

import com.example.reward_monitoring.mission.missionCS.model.CSType;
import com.example.reward_monitoring.mission.missionCS.model.MsnType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.Getter;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;

@Getter
public class MissionCSSearchDto {

    @Schema(description = "CS 타입", example = "정답제목확인,URL확인,데이터유실")
    private CSType csType;

    @Schema(description = "미션 타입",example = "정답,검색,저장")
    private MsnType msnType;

    @Schema(description = "미션시작시간")
    private LocalDate startAtMsn;

    @Schema(description = "미션종료시간")
    private LocalDate endAtMsn;

    @Comment("미션 제목")
    @Column(name = "msn_title")
    @Schema(description = "미션 제목", example = "42871 / 거리측정기")
    private String msnTitle;
}
