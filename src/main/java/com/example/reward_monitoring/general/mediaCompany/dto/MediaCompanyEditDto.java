package com.example.reward_monitoring.general.mediaCompany.dto;


import com.example.reward_monitoring.general.mediaCompany.entity.MediaCompany;
import lombok.Getter;

@Getter
public class MediaCompanyEditDto {

    private String companyName;
    private String companyID;
    private String companyManager;
    private String companyManagerPhoneNum;
    private String APIKey;
    private Boolean isActive;

    public MediaCompany toEntity(){

        return MediaCompany.builder()
                .companyName(companyName)
                .companyID(companyID)
                .companyManager(companyManager)
                .companyManagePhoneNum(companyManagerPhoneNum)
                .APIKey(APIKey)
                .build();
    }
}
