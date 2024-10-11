package com.example.reward_monitoring.mission.answerMsn.entity;


import com.example.reward_monitoring.general.advertiser.entity.Advertiser;
import com.example.reward_monitoring.general.userServer.entity.Server;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.json.JSONArray;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@ToString
@Table(name = "answer_msn")
public class AnswerMsn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "기본키", example = "1")
    private int idx;

    @Builder.Default
    @Comment("미션 기본 수량")
    @Column(name = "mission_default_qty", nullable = false)
    @Schema(description = "미션 기본 수량", example = "50")
    private int missionDefaultQty=50;

    @Builder.Default
    @Comment("미션 데일리 캡")
    @Column(name = "mission_daily_cap", nullable = false)
    @Schema(description = "미션 데일리 캡", example = "50")
    private int missionDailyCap=50 ;

    @Builder.Default
    @Comment("미션 노출 순서")
    @Column(name = "mission_exp_order")
    @Schema(description = "미션 노출 순서", example = "50")
    private int missionExpOrder = 100;


    @Comment("광고주(외래키)")
    @ManyToOne()
    @JoinColumn(name="advertiser", referencedColumnName = "advertiser" , nullable = false )
    @Schema(description = "광고주", example = "원픽")
    Advertiser advertiser;
    
    @Comment("광고주 상세")
    @Column(name = "advertiser_details")
    @Schema(description = "광고주 상세", example = "82652333318")
    private String advertiserDetails;
    
    @Comment("미션 제목")
    @Column(name = "mission_title",nullable = false)
    @Schema(description = "미션 제목", example = "무쇠웍")
    private String missionTitle;

    @Comment("미션 상세 제목")
    @Column(name = "mission_detail_title",nullable = false)
    @Schema(description = "미션 상세 제목", example = "무쇠웍")
    private String missionDetailTitle;


    @Comment("미션 정답")
    @Column(name = "mission_answer",nullable = false)
    @Schema(description = "미션 정답", example = "5107811272")
    private String missionAnswer;

    @Comment("미션 내용")
    @Column(name = "mission_content",length = 500)
    @Schema(description = "미션 내용", example = "상세페이지 하단에 구매 추가정보 클릭후 상품번호 앞 5자리를 입력해주세요.")
    private String missionContent;

    @Comment("미션 시작일시")
    @Column(name = "start_at_msn", nullable = false)
    @Schema(description = "미션 시작일시", example = "2024-09-04 15:00:00")
    private ZonedDateTime startAtMsn;

    @Comment("미션 종료일시")
    @Column(name = "end_at_msn", nullable = false)
    @Schema(description = "미션 종료일시", example = "2024-09-13 23:40:00")
    private ZonedDateTime endAtMsn;

    @Comment("데일리캡 시작일시")
    @Column(name = "start_at", nullable = false)
    @Schema(description = "데일리캡 시작일시", example = "2024-09-04 ")
    private LocalDate startAtCap;

    @Comment("데일리캡 종료일시")
    @Column(name = "end_at", nullable = false)
    @Schema(description = "데일리캡 종료일시", example = "2024-09-13")
    private LocalDate endAtCap;

    @Transient
    private LocalDateTime startAtMsnLocalDateTime;
    @Transient
    private LocalDate startAtMsnLocalDate;
    @Transient
    private LocalTime startAtMsnLocalTime;

    @Transient
    private LocalDateTime endAtMsnLocalDateTime;
    @Transient
    private LocalDate endAtMsnLocalDate;
    @Transient
    private LocalTime endAtMsnLocalTime;


    @Transient
    private String bothAtMsnLocalDateTime;
    @Transient
    private String bothAtCap;

    @Builder.Default
    @Comment("미션 사용여부")
    @Column(name = "mission_active", nullable = false)
    @Schema(description = "미션 사용여부", example = "true")
    private boolean missionActive =false;


    @Builder.Default
    @Comment("미션 삭제여부")
    @Column(name = "is_hidden", nullable = false)
    @Schema(description = "미션 삭제여부", example = "true")
    private boolean isHidden =false;

    @Builder.Default
    @Comment("미션 노출여부")
    @Column(name = "mission_exp", nullable = false)
    @Schema(description = "미션 노출여부", example = "true")
    private boolean missionExposure=false;

    @Builder.Default
    @Comment("중복 참여 가능 여부(+1 Day)")
    @Column(name = "dup_participation", nullable = false)
    @Schema(description = "중복 참여 가능여부", example = "true")
    private boolean dupParticipation=false;

    @Comment("재참여 가능일")
    @Column(name = "re_engagementDay" )
    @Schema(description = "재참여 가능일", example = "1")
    private int reEngagementDay;



    @Comment("참여 제외할 매체 IDX,1|2|3|4|5형식으로 넣어야함")
    @Column(name = "except_Media" ,columnDefinition = "TEXT")
    @Schema(description = "참여 제외할 매체 IDX,1|2|3|4|5형식으로 넣어야함", example = "1|2|3|4|5",nullable = true)
    private String exceptMedia;

    @Transient
    private int[] intArray;



    public JSONArray convertDataToJson(String data){
        String[] elements = data.split("\\|");
        JSONArray jsonArray = new JSONArray();
        for (String element : elements) {
            jsonArray.put(Integer.parseInt(element)); // 정수형으로 변환하여 추가
        }
        return jsonArray;
    }

    public String convertJsonToString(JSONArray jsonArray) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < jsonArray.length(); i++) {
            result.append(jsonArray.getInt(i));

            // 마지막 요소가 아닐 경우에만 '|' 추가
            if (i < jsonArray.length() - 1) {
                result.append("|");
            }
        }

        return result.toString();
    }



    @Comment("미션 URL 1")
    @Column(name = "msn_url1" )
    @Schema(description = "미션 URL1", example = "www.abc.com",nullable = true)
    private String msnUrl1;


    @Comment("미션 URL 2")
    @Column(name = "msn_url2" )
    @Schema(description = "미션 URL2", example = "www.abc.com",nullable = true)
    private String  msnUrl2;


    @Comment("미션 URL 3")
    @Column(name = "msn_url3" )
    @Schema(description = "미션 URL3", example = "www.abc.com",nullable = true)
    private String msnUrl3;


    @Comment("미션 URL 4")
    @Column(name = "msn_url4" )
    @Schema(description = "미션 URL4", example = "www.abc.com",nullable = true)
    private String msnUrl4;


    @Comment("미션 URL 5")
    @Column(name = "msn_url5" )
    @Schema(description = "미션 URL5", example = "www.abc.com",nullable = true)
    private String msnUrl5;


    @Comment("미션 URL 6")
    @Column(name = "msn_url6" )
    @Schema(description = "미션 URL1", example = "www.abc.com",nullable = true)
    private String msnUrl6;


    @Comment("미션 URL 7")
    @Column(name = "msn_url7" )
    @Schema(description = "미션 URL1", example = "www.abc.com",nullable = true)
    private String msnUrl7;


    @Comment("미션 URL 8")
    @Column(name = "msn_url8" )
    @Schema(description = "미션 URL1", example = "www.abc.com",nullable = true)
    private String msnUrl8;


    @Comment("미션 URL 9")
    @Column(name = "msn_url9" )
    @Schema(description = "미션 URL1", example = "www.abc.com",nullable = true)
    private String msnUrl9;

    @Comment("미션 URL 9")
    @Column(name = "msn_url10" )
    @Schema(description = "미션 URL1", example = "www.abc.com",nullable = true)
    private String msnUrl10;

    @Builder.Default
    @Comment("전체 랜딩수")
    @Column(name = "total_landing_cnt")
    @Schema(description = "전체 랜딩수")
    private int totalLandingCnt=0;

    @Builder.Default
    @Comment("전체 참여수")
    @Column(name = "전체 참여수")
    @Schema(description = "전체 참여수")
    private int totalPartCnt=0;

    @Builder.Default
    @Comment("미션 데이터 타입")  // false = 삭제 데이터 , true = 정상 데이터
    @Column(name = "data_type")
    @Schema(description = "미션 데이터 타입")
    private boolean dataType = true;

    @Comment("이미지 파일")
    @Lob
    @Column(name = "image_data", columnDefinition = "MEDIUMBLOB")
    @Schema(description = "이미피 파일")
    private byte[] imageData;

    @Comment("이미지 파일명")
    @Column(name = "image_name")
    @Schema(description = "이미피 파일명")
    private String imageName;

    @Comment("서버URL(외래키)")
    @ManyToOne()
    @JoinColumn(name="server_url", referencedColumnName = "server_url", nullable = true)
    @Schema(description = "서버URL(외래키)")
    Server server;




    @Builder
    public AnswerMsn(int missionDefaultQty,int missionDailyCap,Advertiser advertiser,String advertiserDetails
    ,String missionTitle,String missionDetailTitle,String missionAnswer,String missionContent,ZonedDateTime startAtMsn,
                     ZonedDateTime endAtMsn,LocalDate  startAtCap,LocalDate endAtCap,boolean missionActive,boolean missionExposure,
                     boolean dupParticipation,int reEngagementDay,String exceptMedia,String msnUrl1,String msnUrl2,String msnUrl3,String msnUrl4,
                     String msnUrl5,String msnUrl6,String msnUrl7,String msnUrl8,String msnUrl9,String msnUrl10,byte[]imageData,String imageName,Server server) {

        this.missionDefaultQty = missionDefaultQty;
        this.missionDailyCap = missionDailyCap;
        this.advertiser = advertiser;
        this.advertiserDetails = advertiserDetails;
        this.missionTitle = missionTitle;
        this.missionDetailTitle = missionDetailTitle;
        this.missionAnswer =missionAnswer;
        this.missionContent = missionContent;
        this.startAtMsn = startAtMsn;
        this.endAtMsn = endAtMsn;
        this.startAtCap = startAtCap;
        this.endAtCap = endAtCap;
        this.missionActive = missionActive;
        this.missionExposure = missionExposure;
        this.dupParticipation = dupParticipation;
        this.reEngagementDay = reEngagementDay;
        this.exceptMedia =   convertJsonToString(convertDataToJson(exceptMedia));
        this.msnUrl1 = msnUrl1;
        this.msnUrl2 = msnUrl2;
        this.msnUrl3 = msnUrl3;
        this.msnUrl4 = msnUrl4;
        this.msnUrl5 = msnUrl5;
        this.msnUrl6 = msnUrl6;
        this.msnUrl7 = msnUrl7;
        this.msnUrl8 = msnUrl8;
        this.msnUrl9 = msnUrl9;
        this.msnUrl10 = msnUrl10;
        this.imageData = imageData;
        this.imageName = imageName;
        this.server = server;
    }
    @PostLoad
    public void changeDTypeDateTime() {
        this.startAtMsnLocalDateTime = this.startAtMsn.toLocalDateTime().plusHours(9);
        this.startAtMsnLocalDate = this.startAtMsn.plusHours(9).toLocalDate();
        this.startAtMsnLocalTime = this.startAtMsn.toLocalTime().plusHours(9);
        this.endAtMsnLocalDateTime = this.endAtMsn.toLocalDateTime().minusHours(9);
        this.endAtMsnLocalDate = this.endAtMsn.plusHours(9).toLocalDate();
        this.endAtMsnLocalTime = this.endAtMsn.toLocalTime().plusHours(9);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        bothAtMsnLocalDateTime= startAtMsnLocalDateTime.format(formatter) + " ~ " + endAtMsnLocalDateTime.format(formatter);

        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        bothAtCap= startAtCap.format(formatter) + " ~ " + endAtCap.format(formatter);

    }
}


