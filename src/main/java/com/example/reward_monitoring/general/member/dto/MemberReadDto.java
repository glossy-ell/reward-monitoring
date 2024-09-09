package com.example.reward_monitoring.general.member.dto;

import com.example.reward_monitoring.general.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "회원 수정 요청 DTO")
public class MemberReadDto {


    @Schema(description = "ID")
    private String id;
    @Schema(description = "password")
    private String password;
    @Schema(description = "계정이름")
    private String name;
    @Schema(description = "부서")
    private String department;

    public Member toEntity(){

        return Member.builder()
                .id(id)
                .password(password)
                .name(name)
                .department(department)
                .build();
    }
}
