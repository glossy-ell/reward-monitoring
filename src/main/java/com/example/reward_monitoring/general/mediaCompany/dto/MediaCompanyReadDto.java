package com.example.reward_monitoring.general.mediaCompany.dto;


import com.example.reward_monitoring.general.mediaCompany.entity.MediaCompany;
import lombok.Getter;

@Getter
public class MediaCompanyReadDto {
    private String companyName;
    private String companyID;
    private String companyManager;
    private String companyManagerPhoneNum;
    private String APIKey;

    public MediaCompany toEntity(){

        if(companyManager ==null)
            companyManager="미정";
        if(companyManagerPhoneNum ==null)
            companyManagerPhoneNum="미정";

        return MediaCompany.builder()
                .companyName(companyName)
                .companyID(companyID)
                .companyManager(companyManager)
                .companyManagePhoneNum(companyManagerPhoneNum)
                .APIKey(APIKey)
                .build();
    }
}
