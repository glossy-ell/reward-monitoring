package com.example.reward_monitoring.general.userServer.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@ToString
@EqualsAndHashCode
@Table(name = "servers")
public class Server {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "기본키", example = "1")
    private int idx;

    @Column(name = "server_name", nullable = false)
    @Schema(description = "서버명")
    private String serverName;

    @Column(name = "server_url", nullable = false ,unique = true)
    @Schema(description = "서버URL")
    private String serverUrl;

    @Comment("활성여부")
    @Column(name = "is_active", nullable = false)
    @Schema(description = "활성여부")
    private boolean isActive;

    @Comment("생성일자")
    @Column(name = "created_at", nullable = false, updatable = false)
    @Schema(description = "생성일자")
    private ZonedDateTime createdAt;

    @Transient
    private LocalDateTime createdAtLocalDateTime;
    @Transient
    private LocalDate createdAtLocalDate;


    @Comment("서버 통신 키")
    @Column(name = "server_key", nullable = false,unique = true)
    @Schema(description = "서버 통신 키")
    private String serverKey;


    @Comment("관리자 메모")
    @Column(name = "memo")
    @Schema(hidden = true)
    private String memo;



    @PrePersist
    protected void onCreate() {
        createdAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
    }

    @Builder
    public Server(String serverName,String serverUrl,boolean isActive, String serverKey, String memo) {
        this.serverName = serverName;
        this.serverUrl = serverUrl;
        this.isActive = isActive;
        this.serverKey = serverKey;
        this.memo = memo;
    }
    @PostLoad
    public void changeDTypeDateTime(){

        this.createdAtLocalDateTime = this.createdAt.toLocalDateTime();
        this.createdAtLocalDate = this.createdAt.toLocalDate();

    }
}
