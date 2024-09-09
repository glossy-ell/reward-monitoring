package com.example.reward_monitoring.general.member.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "회원 수정 요청 DTO")
public class MemberEditDto {

    @Schema(description = "변경할 비밀번호", example = "12345678")
    private String changedPassword;
    @Schema(description = "변경할 전화번호", example = "01012345678")
    private Integer phone;
    @Schema(description = "계정 활성상태 여부", example = "true")
    private Boolean isActive;
}
