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
    @Schema(description = "계정 활성상태 여부", example = "true")
    private Boolean isActive;


    public Advertiser toEntity(){

        return Advertiser.builder()
                .advertiser(advertiser)
                .manager(manager)
                .managerPhoneNum(managerPhoneNum)
                .isActive(isActive)
                .build();
    }
}
