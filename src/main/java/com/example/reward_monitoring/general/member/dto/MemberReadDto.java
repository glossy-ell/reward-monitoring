package com.example.reward_monitoring.general.member.dto;

import com.example.reward_monitoring.general.member.entity.Member;
import com.example.reward_monitoring.general.member.model.CtryCode;
import com.example.reward_monitoring.general.member.model.Lang;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import org.hibernate.annotations.Comment;

@Getter
@Schema(description = "회원 가입 요청 DTO")
public class MemberReadDto {


    @Schema(description = "ID")
    private String id;
    @Schema(description = "password")
    private String password;
    @Schema(description = "계정이름")
    private String name;
    @Schema(description = "부서")
    private String department;
    @Schema(description = "지역 코드",example = " KOR,HKG,USA,JPN,CHN")
    private CtryCode ctryCode;
    @Schema(description = "전화번호",example = "01012345678")
    private String phone;
    @Schema(description = "기본 언어",example = "한국어,English,中文,日本語")
    private Lang lang;

    public Member toEntity(){

        return Member.builder()
                .id(id)
                .password(password)
                .name(name)
                .department(department)
                .ctryCode(ctryCode)
                .phone(phone)
                .lang(lang)
                .build();
    }
}
