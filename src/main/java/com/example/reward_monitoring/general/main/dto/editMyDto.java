package com.example.reward_monitoring.general.main.dto;


import com.example.reward_monitoring.general.member.model.CtryCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class editMyDto {
    @Schema(description = "비밀번호",example = "1234")
    private String password;
    @Schema(description = "전화번호",example = "01012345678")
    private String phoneNum;
    @Schema(description = "지역 코드",example = "+82,+852,+1,+81,+86")
    private CtryCode ctryCode;
}
