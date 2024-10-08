package com.example.reward_monitoring.general.mediaCompany.dto;



import com.example.reward_monitoring.general.mediaCompany.model.Type;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MediaCompanyEditDto {

    @Schema(description = "매체사명")
    private String companyName;
    @Schema(description = "서버 URL",example = "https://ocb.srk.co.kr")
    private String serverUrl;
    @Schema(description = "매체사 ID")
    private String companyID;
    @Schema(description = "매체사 담당자")
    private String companyManager;
    @Schema(description = "비밀번호")
    private String password;
    @Schema(description = "매체사 담당자 연락처")
    private String companyManagerPhoneNum;
    @Schema(description = "API Key")
    private String APIKey;
    @Schema(description = "활성 여부")
    private Boolean isActive;
    @Schema(description = "매체사 개발/운영")
    private Type type;
    @Schema(description = "생성일시")
    private String companyReturnUrl;
    @Schema(description = "매체사 리턴 파라미터 ")
    private String companyReturnParameter;
    @Schema(description = "관리자 메모 ")
    private String memo;
    @Schema(description = "관리자 메모 ",example = "{\"quiz\":5,\"search\":5,\"sightseeing\":5}")
    private String companyUserSaving;


}
