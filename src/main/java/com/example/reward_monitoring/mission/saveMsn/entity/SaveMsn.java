package com.example.reward_monitoring.mission.saveMsn.entity;


import com.example.reward_monitoring.general.advertiser.entity.Advertiser;
import com.example.reward_monitoring.general.mediaCompany.entity.MediaCompany;
import com.example.reward_monitoring.general.userServer.entity.Server;

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
@Table(name = "save_msn")
public class SaveMsn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idx;

    @Builder.Default
    @Comment("미션 기본 수량")
    @Column(name = "mission_default_qty")
    @Schema(description = "미션 기본 수량", example = "50")
    private int missionDefaultQty = 50;

    @Builder.Default
    @Comment("미션 데일리 캡")
    @Column(name = "mission_daily_cap")
    @Schema(description = "미션 데일리 캡", example = "50")
    private int missionDailyCap = 50;

    @Builder.Default
    @Comment("미션 노출 순서")
    @Column(name = "mission_exp_order")
    @Schema(description = "미션 노출 순서", example = "100")
    private int missionExpOrder = 100;



    @Comment("광고주(외래키)")
    @ManyToOne()
    @JoinColumn(name="advertiser", referencedColumnName = "advertiser" )
    Advertiser advertiser;

    @Comment("광고주 상세")
    @Column(name = "advertiser_details" )
    @Schema(description = "광고주 상세", example = "-")
    private String advertiserDetails;

    @Comment("미션 제목")
    @Column(name = "mission_title",nullable = false)
    @Schema(description = "미션 제목", example = "-")
    private String missionTitle;

    @Comment("미션 상세 제목")
    @Column(name = "mission_detail_title",nullable = false)
    @Schema(description = "미션 상세 제목", example = "아주 단단한 무쇠웍")
    private String missionDetailTitle;

    @Comment("미션 내용")
    @Column(name = "mission_content",length = 500)
    @Schema(description = "미션 내용", example = "상세페이지 하단에 구매 추가정보 클릭후 상품번호 앞 5자리를 입력해주세요.")
    private String missionContent;


    @Comment("미션 시작일시")
    @Column(name = "start_at_msn", nullable = false)
    @Schema(description = "미션 시작일시", example = "2024-09-04 15:00:00")
    private LocalDateTime startAtMsn;

    @Comment("미션 종료일시")
    @Column(name = "end_at_msn", nullable = false)
    @Schema(description = "미션 종료일시", example = "2024-09-13 23:40:00")
    private LocalDateTime endAtMsn;

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
    @Comment("중복 참여 가능 여부(+1 Day)")
    @Column(name = "dup_participation", nullable = false)
    @Schema(description = "중복 참여 가능여부", example = "true")
    private boolean dupParticipation;

    @Builder.Default
    @Comment("미션 사용여부")
    @Column(name = "mission_active", nullable = false)
    @Schema(description = "미션 사용여부", example = "true")
    private boolean missionActive =true;

    @Comment("미션 노출여부")
    @Column(name = "mission_exp", nullable = false)
    @Schema(description = "미션 노출여부", example = "true")
    private boolean missionExposure;


    @Comment("재참여 가능일")
    @Column(name = "re_engagementDay" )
    @Schema(description = "재참여 가능일", example = "1")
    private Integer reEngagementDay;

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


    @Comment("미션 URL")
    @Column(name = "msn_url" )
    @Schema(description = "미션 URL", example = "www.abc.com",nullable = true)
    private String msnUrl;

    @Comment("미션 최종 URL")
    @Column(name = "msn_final_url" )
    @Schema(description = "미션 최종 URL", example = "www.abc.com",nullable = true)
    private String msnFinalUrl;

    @Comment("검색 키워드")
    @Column(name = "search_keyword",nullable = false)
    private String searchKeyword;

    @Builder.Default
    @Comment("전체 랜딩수")
    @Column(name = "total_landing_cnt")
    @Schema(description = "전체 랜딩수")
    private int totalLandingCnt=0;

    @Builder.Default
    @Comment("전체 참여수")
    @Column(name = "total_part_cnt")
    @Schema(description = "전체 참여수")
    private int totalPartCnt=0;

    @Builder.Default
    @Comment("미션 데이터 타입")  // false = 삭제 데이터 , true = 정상 데이터
    @Column(name = "data_type")
    @Schema(description = "미션 데이터 타입")
    private boolean dataType = true;



    @Comment("이미지 파일명")
    @Column(name = "image_name")
    @Schema(description = "이미피 파일명")
    private String imageName;

    @Comment("이미지 경로")
    @Column(name = "image_path")
    @Schema(description = "이미지 경로")
    private String imagePath;

    @Comment("서버URL(외래키)")
    @ManyToOne()
    @JoinColumn(name="server_url", referencedColumnName = "server_url",nullable = true)
    @Schema(description = "서버URL(외래키)")
    Server server;

    @Comment("매체사 idx")
    @ManyToOne()
    @JoinColumn(name="mediacompany_idx", referencedColumnName = "idx", nullable = true)
    @Schema(description = "매체사 idx")
    MediaCompany mediaCompany;

    @Builder
    public SaveMsn(int missionDefaultQty,int missionDailyCap,int missionExpOrder,Advertiser advertiser,String advertiserDetails
            ,String missionTitle,String missionDetailTitle,String missionContent,LocalDateTime startAtMsn,LocalDateTime endAtMsn
            ,LocalDate startAtCap,LocalDate endAtCap,boolean missionExposure
            ,boolean dupParticipation,int reEngagementDay,String exceptMedia,String msnUrl,String msnFinalUrl,String searchKeyword,byte[]imageData,
                   String imageName,Server server) {
        this.missionDefaultQty = missionDefaultQty;
        this.missionDailyCap = missionDailyCap;
        this.advertiser = advertiser;
        this.advertiserDetails = advertiserDetails;
        this.missionTitle = missionTitle;
        this.missionDetailTitle = missionDetailTitle;
        this.missionContent = missionContent;
        this.startAtMsn = startAtMsn;
        this.endAtMsn = endAtMsn;
        this.startAtCap = startAtCap;
        this.endAtCap = endAtCap;
        this.missionExposure = missionExposure;
        this.dupParticipation = dupParticipation;
        this.reEngagementDay = reEngagementDay;
        this.exceptMedia= convertJsonToString(convertDataToJson(exceptMedia));
        this.searchKeyword = searchKeyword;
        this.msnUrl = msnUrl;
        this.msnFinalUrl =msnFinalUrl;
        this.imageName = imageName;
        this.server = server;
    }

    @PostLoad
    public void changeDTypeDateTime() {
        this.startAtMsnLocalDate = this.startAtMsn.toLocalDate();
        this.startAtMsnLocalTime = this.startAtMsn.toLocalTime();

        this.endAtMsnLocalDate = this.endAtMsn.toLocalDate();
        this.endAtMsnLocalTime = this.endAtMsn.toLocalTime();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        bothAtMsnLocalDateTime= startAtMsn.format(formatter) + " ~ " + endAtMsn.format(formatter);

        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        bothAtCap= startAtCap.format(formatter) + " ~ " + endAtCap.format(formatter);
    }
}
