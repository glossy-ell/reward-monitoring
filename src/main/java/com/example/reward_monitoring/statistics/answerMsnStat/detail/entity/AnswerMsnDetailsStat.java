package com.example.reward_monitoring.statistics.answerMsnStat.detail.entity;

import com.example.reward_monitoring.general.advertiser.entity.Advertiser;
import com.example.reward_monitoring.general.mediaCompany.entity.MediaCompany;
import com.example.reward_monitoring.general.userServer.entity.Server;
import com.example.reward_monitoring.mission.answerMsn.entity.AnswerMsn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;


import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@ToString
@Table(name = "answer_msn_details_stat")
public class AnswerMsnDetailsStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "기본키", example = "1")
    private int TX;

    @Comment("매체사 IDX")
    @ManyToOne()
    @JoinColumns({
            @JoinColumn(name = "mediaCompany_idx", referencedColumnName = "idx"),
            @JoinColumn(name = "company_name", referencedColumnName = "company_name")
    })
    @Schema(description = "매체사 IDX", example = "3")
    MediaCompany mediaCompany;

    @Comment("미션 정보")
    @ManyToOne()
    @JoinColumns({
            @JoinColumn(name = "answer_msn_idx", referencedColumnName = "idx"),
            @JoinColumn(name = "mission_title" , referencedColumnName =  "mission_title")
    })
    @Schema(description = "정답 미션  정보", example = "3")
    AnswerMsn answerMsn;

    @Comment("광고주 정보)")
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumns({
            @JoinColumn(name = "advertiser_idx", referencedColumnName = "idx"),
            @JoinColumn(name = "advertiser", referencedColumnName = "advertiser")
    })
    @Schema(description = "광고주 정보")
    Advertiser advertiser;

    @Comment("서버URL(외래키)")
    @ManyToOne()
    @JoinColumn(name="server_url", referencedColumnName = "server_url")
    @Schema(description = "서버URL(외래키)")
    Server server;

    @Comment("회원 ID")
    @Column(name = "member_id")
    @Schema(description = "회원 ID" ,example = "202309141619265927")
    private String memberId;


    @Comment("포인트")
    @Column(name = "point", nullable = false)
    @Schema(description = "포인트", example = "5")
    private int point;


    @Comment("어뷰상태")  // true = 어뷰,false = 정상
    @Column(name = "is_abuse")
    @Schema(description = "미션 데이터 타입",example = "true,false")
    private boolean isAbuse;

    @Comment("응답 확인")  // true = 성공, false = 실패
    @Column(name = "response")
    @Schema(description = "미션 데이터 타입",example = "true,false")
    private boolean response;

    @Comment("IP")  // true = 성공, false = 실패
    @Column(name = "ip")
    @Schema(description = "IP",example = "211.200.234.31")
    private String ip;

    @Comment("등록일시")
    @Column(name = "registration_date", nullable = false)
    @Schema(description = "등록일시", example = "2024-09-13 16:01:51")
    private LocalDateTime registrationDate;


    @Builder
    public AnswerMsnDetailsStat(MediaCompany mediaCompany,Advertiser advertiser , Server server,String memberId, AnswerMsn answerMsn
            , int point, boolean isAbuse,boolean response,String ip,LocalDateTime registrationDate) {

        this.mediaCompany = mediaCompany;
        this.answerMsn=answerMsn;
        this.advertiser = advertiser;
        this.server = server;
        this.memberId=memberId;
        this.point = point;
        this.isAbuse =isAbuse;
        this.response = response;
        this.ip = ip;
        this.registrationDate = registrationDate;

    }

}
