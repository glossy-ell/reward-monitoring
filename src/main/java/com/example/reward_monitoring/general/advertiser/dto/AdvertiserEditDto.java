package com.example.reward_monitoring.general.advertiser.dto;

import com.example.reward_monitoring.general.advertiser.entity.Advertiser;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class AdvertiserEditDto {

    @Schema(description = "광고주")
    private String advertiser;
    @Schema(description = "광고주 담당자")
    private String manager;
    @Schema(description = "광고주 담당자 연락처")
    private String managerPhoneNum;
    @Schema(description = "활성여부")
    private Boolean isActive;
    @Schema(description = "관리자 메모")
    private String memo;

}
