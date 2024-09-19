package com.example.reward_monitoring.general.mediaCompany.dto;


import com.example.reward_monitoring.general.mediaCompany.entity.MediaCompany;
import com.example.reward_monitoring.general.mediaCompany.model.Type;
import com.example.reward_monitoring.general.userServer.entity.Server;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class MediaCompanyReadDto {
    @Schema(description = "생성일시")
    private String companyName;
    @Schema(description = "서버 URL")
    private String serverUrl;
    @Schema(description = "매체사 ID")
    private String companyID;
    @Schema(description = "매체사 비밀번호")
    private String password;
    @Schema(description = "매체사 담당자")
    private String companyManager;
    @Schema(description = "매체사 담당자 연락처")
    private String companyManagerPhoneNum;
    @Schema(description = "API Key")
    private String apiKey;
    @Schema(description = "활성 여부")
    private Boolean isActive;
    @Schema(description = "매체사 개발/운영")
    private Type type;
    @Schema(description = "생성일시")
    private String companyReturnUrl;
    @Schema(description = "매체사 리턴 파라미터 ")
    private String companyReturnParameter;
    @Schema(description = "매체사 유저 적립금 ")
    private  int companyUserSaving;
    @Schema(description = "관리자 메모 ")
    private String memo;


    public MediaCompany toEntity(Server serverEntity){

        return MediaCompany.builder()
                .companyName(companyName)
                .server(serverEntity)
                .companyID(companyID)
                .password(password)
                .companyManager(companyManager)
                .companyManagePhoneNum(companyManagerPhoneNum)
                .APIKey(apiKey)
                .isActive(isActive)
                .type(type)
                .companyReturnUrl(companyReturnUrl)
                .companyReturnParameter(companyReturnParameter)
                .companyUserSaving(companyUserSaving)
                .memo(memo)
                .build();
    }
}
