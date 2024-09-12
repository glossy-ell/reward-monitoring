package com.example.reward_monitoring.general.member.model;

import lombok.Getter;

@Getter
public enum CtryCode {
    KOR("+82"),
    HKG("+852"),
    USA("+1"),
    JPN("+81"),
    CHN("+86");

    private final String code;

    // 생성자 정의
    CtryCode(String code) {
        this.code = code;
    }

    // getter 메서드
    public String getCode() {
        return code;
    }
}
