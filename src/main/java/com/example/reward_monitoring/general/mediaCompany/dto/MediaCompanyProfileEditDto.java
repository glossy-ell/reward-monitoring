package com.example.reward_monitoring.general.mediaCompany.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MediaCompanyProfileEditDto {
    @Schema(description = "매체사 비밀번호")
    private String password;
}
